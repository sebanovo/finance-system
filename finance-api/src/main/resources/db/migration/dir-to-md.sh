#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'EOF'
Usage:
  ./dir-to-md.sh [directory] [output.md]

Behavior:
  - If no directory is provided, uses the current working directory.
  - If no output file is provided, writes <directory-basename>.md in the current working directory.
  - Walks the directory recursively and writes every file into a Markdown document.
  - When run without arguments in an interactive terminal, shows a small menu.

Examples:
  ./dir-to-md.sh
  ./dir-to-md.sh /path/to/dir
  ./dir-to-md.sh /path/to/dir /tmp/output.md
EOF
}

resolve_abs_dir() {
  local input_dir="$1"
  if [[ ! -d "$input_dir" ]]; then
    printf 'Error: directory not found: %s\n' "$input_dir" >&2
    exit 1
  fi
  (cd "$input_dir" && pwd -P)
}

abs_path() {
  local path="$1"
  if [[ -e "$path" ]]; then
    (cd "$(dirname "$path")" && printf '%s/%s\n' "$(pwd -P)" "$(basename "$path")")
  else
    local parent
    parent="$(dirname "$path")"
    if [[ -d "$parent" ]]; then
      (cd "$parent" && printf '%s/%s\n' "$(pwd -P)" "$(basename "$path")")
    else
      printf '%s\n' "$path"
    fi
  fi
}

guess_language() {
  local file_name="$1"
  case "${file_name##*.}" in
    md) printf 'markdown' ;;
    sh) printf 'bash' ;;
    yml|yaml) printf 'yaml' ;;
    json) printf 'json' ;;
    xml) printf 'xml' ;;
    sql) printf 'sql' ;;
    java) printf 'java' ;;
    kt) printf 'kotlin' ;;
    js) printf 'javascript' ;;
    ts) printf 'typescript' ;;
    css) printf 'css' ;;
    html) printf 'html' ;;
    properties) printf 'properties' ;;
    txt) printf 'text' ;;
    *) printf 'text' ;;
  esac
}

is_text_file() {
  local file_path="$1"
  if command -v file >/dev/null 2>&1; then
    local mime
    mime="$(file --brief --mime-type "$file_path" 2>/dev/null || true)"
    case "$mime" in
      text/*|application/json|application/xml|application/javascript|application/x-yaml|application/x-sh|application/x-ndjson)
        return 0
        ;;
      *)
        return 1
        ;;
    esac
  fi

  grep -Iq . "$file_path"
}

TARGET_DIR="${1:-.}"
OUTPUT_FILE="${2:-}"

if [[ "${1:-}" == "-h" || "${1:-}" == "--help" ]]; then
  usage
  exit 0
fi

if [[ $# -eq 0 && -t 0 ]]; then
  printf 'Directory to Markdown export\n\n'
  printf '1) Use current directory\n'
  printf '   Use the folder where you are currently standing in the terminal.\n'
  printf '2) Enter a custom directory\n'
  printf '   Type a path relative to the current folder or an absolute path.\n'
  printf '   Example: src/main/resources/db/migration/tenant\n'
  printf '   Do not start with /src/... unless you really mean an absolute path from root.\n'
  read -r -p 'Choose an option [1-2]: ' MENU_OPTION

  case "${MENU_OPTION:-1}" in
    1)
      TARGET_DIR="."
      ;;
    2)
      read -r -p 'Enter the directory path: ' TARGET_DIR
      ;;
    *)
      printf 'Invalid option. Using current directory.\n'
      TARGET_DIR="."
      ;;
  esac

  printf '\nOutput file path:\n'
  printf '   - Press Enter to use the default name in the current folder.\n'
  printf '   - Or type a filename like tenant-export.md to save it here.\n'
  printf '   - You can also give a path like exports/tenant-export.md.\n'
  read -r -p 'Output file path [press Enter for default]: ' OUTPUT_FILE
fi

TARGET_DIR_ABS="$(resolve_abs_dir "$TARGET_DIR")"
TARGET_BASE="$(basename "$TARGET_DIR_ABS")"

if [[ -z "$OUTPUT_FILE" ]]; then
  OUTPUT_FILE="./${TARGET_BASE}.md"
fi

OUTPUT_FILE_ABS="$(abs_path "$OUTPUT_FILE")"

{
  printf '# Directory Export: %s\n\n' "$TARGET_DIR_ABS"
  printf '_Generated on %s_\n\n' "$(date -u +"%Y-%m-%d %H:%M:%SZ")"
  printf '## Summary\n\n'
  printf '%s\n' "- Source directory: \`$TARGET_DIR_ABS\`"
  printf '%s\n\n' "- Output file: \`$OUTPUT_FILE_ABS\`"
  printf '## Files\n\n'

  mapfile -d '' FILES < <(find "$TARGET_DIR_ABS" -type f -print0 | sort -z)

  if [[ "${#FILES[@]}" -eq 0 ]]; then
    printf '_No files found in the directory._\n'
    exit 0
  fi

  for file_path in "${FILES[@]}"; do
    if [[ "$file_path" == "$OUTPUT_FILE_ABS" ]]; then
      continue
    fi

    rel_path="${file_path#"$TARGET_DIR_ABS"/}"
    lang="$(guess_language "$file_path")"

    printf '### `%s`\n\n' "$rel_path"

    if is_text_file "$file_path"; then
      printf '```%s\n' "$lang"
      cat "$file_path"
      printf '\n```\n\n'
    else
      mime_type=""
      if command -v file >/dev/null 2>&1; then
        mime_type="$(file --brief --mime-type "$file_path" 2>/dev/null || true)"
      fi
      printf '_Binary or non-text file omitted._\n\n'
      if [[ -n "$mime_type" ]]; then
        printf '%s\n' "- MIME type: \`$mime_type\`"
      fi
      printf '%s\n\n' "- Size: \`$(wc -c < "$file_path")\` bytes"
    fi
  done
} > "$OUTPUT_FILE"

printf 'Markdown export written to: %s\n' "$OUTPUT_FILE_ABS"
printf 'Default save location is the current working directory unless you enter another output path.\n'
