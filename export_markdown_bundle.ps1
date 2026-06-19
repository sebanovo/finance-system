param(
    [string]$OutputDir = "markdown-export"
)

$ErrorActionPreference = "Stop"

$Root = Split-Path -Parent $MyInvocation.MyCommand.Path
$OutputPath = Join-Path $Root $OutputDir

$GeneratedFiles = @(
    "backend-completo-infraestructura.md",
    "frontend-completo.md",
    "mobile-completo.md",
    "env-yaml-configuracion.md"
)

$ExcludedDirectories = @(
    ".git",
    ".angular",
    ".dart_tool",
    ".idea",
    ".vscode",
    "build",
    "coverage",
    "dist",
    "node_modules",
    "Pods",
    ".gradle",
    ".pub-cache",
    $OutputDir
)

$BinaryExtensions = @(
    ".a",
    ".apk",
    ".bin",
    ".class",
    ".dll",
    ".dylib",
    ".exe",
    ".gif",
    ".ico",
    ".jar",
    ".jpg",
    ".jpeg",
    ".keystore",
    ".lockb",
    ".mp3",
    ".mp4",
    ".pdf",
    ".png",
    ".so",
    ".ttf",
    ".webp",
    ".woff",
    ".woff2",
    ".zip"
)

$ConfigNamePatterns = @(
    ".env*",
    "*.conf",
    "*.config.*",
    "*.dockerignore",
    "*.gradle",
    "*.gradle.kts",
    "*.json",
    "*.lock",
    "*.plist",
    "*.properties",
    "*.rc",
    "*.sample",
    "*.toml",
    "*.xml",
    "*.xcconfig",
    "*.yaml",
    "*.yml",
    "Dockerfile*",
    "CMakeLists.txt",
    "pubspec.*",
    "package*.json",
    "tsconfig*.json",
    "angular.json",
    "components.json",
    "eslint.config.js",
    "analysis_options.yaml",
    "devtools_options.yaml"
)

function Convert-ToRelativePath {
    param([string]$Path)

    return [System.IO.Path]::GetRelativePath($Root, $Path).Replace("\", "/")
}

function Test-IsExcludedDirectory {
    param([System.IO.FileInfo]$File)

    $relativePath = Convert-ToRelativePath $File.FullName
    $parts = $relativePath -split "/"

    foreach ($part in $parts) {
        if ($ExcludedDirectories -contains $part) {
            return $true
        }
    }

    return $false
}

function Test-IsBinaryFile {
    param([System.IO.FileInfo]$File)

    if ($BinaryExtensions -contains $File.Extension.ToLowerInvariant()) {
        return $true
    }

    try {
        $bytes = [System.IO.File]::ReadAllBytes($File.FullName)
        $sampleSize = [Math]::Min($bytes.Length, 8192)
        for ($i = 0; $i -lt $sampleSize; $i++) {
            if ($bytes[$i] -eq 0) {
                return $true
            }
        }
    }
    catch {
        return $true
    }

    return $false
}

function Get-CodeFenceLanguage {
    param([string]$Path)

    $name = [System.IO.Path]::GetFileName($Path).ToLowerInvariant()
    $extension = [System.IO.Path]::GetExtension($Path).ToLowerInvariant()

    switch ($name) {
        "dockerfile" { return "dockerfile" }
        "dockerfile.dev" { return "dockerfile" }
        "dockerfile.prod" { return "dockerfile" }
        "cmakelists.txt" { return "cmake" }
        default {}
    }

    switch ($extension) {
        ".css" { return "css" }
        ".dart" { return "dart" }
        ".env" { return "dotenv" }
        ".gradle" { return "gradle" }
        ".html" { return "html" }
        ".js" { return "javascript" }
        ".json" { return "json" }
        ".kt" { return "kotlin" }
        ".kts" { return "kotlin" }
        ".md" { return "markdown" }
        ".py" { return "python" }
        ".sh" { return "bash" }
        ".swift" { return "swift" }
        ".ts" { return "typescript" }
        ".xml" { return "xml" }
        ".yaml" { return "yaml" }
        ".yml" { return "yaml" }
        default { return "" }
    }
}

function Get-ProjectFiles {
    param(
        [string[]]$Paths,
        [scriptblock]$Filter = { param($File) return $true }
    )

    $files = New-Object System.Collections.Generic.List[System.IO.FileInfo]

    foreach ($path in $Paths) {
        $fullPath = Join-Path $Root $path
        if (-not (Test-Path $fullPath)) {
            continue
        }

        if ((Get-Item $fullPath) -is [System.IO.DirectoryInfo]) {
            $items = Get-ChildItem -Path $fullPath -Recurse -File -Force
        }
        else {
            $items = @(Get-Item $fullPath)
        }

        foreach ($item in $items) {
            if (Test-IsExcludedDirectory $item) {
                continue
            }
            if (Test-IsBinaryFile $item) {
                continue
            }
            if (& $Filter $item) {
                $files.Add($item)
            }
        }
    }

    return $files |
        Sort-Object @{ Expression = { Convert-ToRelativePath $_.FullName } } -Unique
}

function Test-IsConfigFile {
    param([System.IO.FileInfo]$File)

    $relativePath = Convert-ToRelativePath $File.FullName
    $name = $File.Name

    foreach ($pattern in $ConfigNamePatterns) {
        if ($name -like $pattern -or $relativePath -like $pattern) {
            return $true
        }
    }

    return $false
}

function Write-MarkdownBundle {
    param(
        [string]$Title,
        [string]$Description,
        [System.IO.FileInfo[]]$Files,
        [string]$OutputFileName
    )

    $targetPath = Join-Path $OutputPath $OutputFileName
    $writer = New-Object System.IO.StreamWriter($targetPath, $false, [System.Text.UTF8Encoding]::new($false))

    try {
        $writer.WriteLine("# $Title")
        $writer.WriteLine()
        $writer.WriteLine($Description)
        $writer.WriteLine()
        $writer.WriteLine("Generado: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')")
        $writer.WriteLine("Total de archivos incluidos: $($Files.Count)")
        $writer.WriteLine()
        $writer.WriteLine("## Indice")
        $writer.WriteLine()

        foreach ($file in $Files) {
            $relativePath = Convert-ToRelativePath $file.FullName
            $anchor = $relativePath.ToLowerInvariant() -replace "[^a-z0-9\s/-]", "" -replace "\s+", "-" -replace "/", ""
            $writer.WriteLine("- [$relativePath](#$anchor)")
        }

        foreach ($file in $Files) {
            $relativePath = Convert-ToRelativePath $file.FullName
            $language = Get-CodeFenceLanguage $relativePath
            $content = Get-Content -LiteralPath $file.FullName -Raw -ErrorAction Stop
            if ($null -eq $content) {
                $content = ""
            }

            $writer.WriteLine()
            $writer.WriteLine("---")
            $writer.WriteLine()
            $writer.WriteLine("## $relativePath")
            $writer.WriteLine()
            $writer.WriteLine("````$language")
            $writer.Write($content)
            if (-not $content.EndsWith("`n")) {
                $writer.WriteLine()
            }
            $writer.WriteLine("````")
        }
    }
    finally {
        $writer.Dispose()
    }

    return $targetPath
}

New-Item -ItemType Directory -Path $OutputPath -Force | Out-Null

foreach ($fileName in $GeneratedFiles) {
    $oldFile = Join-Path $OutputPath $fileName
    if (Test-Path $oldFile) {
        Remove-Item -LiteralPath $oldFile -Force
    }
}

$backendFiles = Get-ProjectFiles -Paths @(
    "mechanic-api",
    "nginx",
    "docker-compose.yml",
    "docker-compose.prod.yml",
    "docker.compose.prod.local.yml",
    "README.md"
)

$frontendFiles = Get-ProjectFiles -Paths @("mechanic-web")

$mobileFiles = Get-ProjectFiles -Paths @("mechanic_mobile")

$configFiles = Get-ProjectFiles -Paths @(".") -Filter {
    param($File)

    $relativePath = Convert-ToRelativePath $File.FullName
    if ($GeneratedFiles -contains $File.Name) {
        return $false
    }

    return Test-IsConfigFile $File
}

$outputs = @()
$outputs += Write-MarkdownBundle `
    -Title "Backend completo e infraestructura" `
    -Description "Codigo completo del backend (`mechanic-api`) junto con infraestructura de despliegue en Docker y Nginx." `
    -Files $backendFiles `
    -OutputFileName "backend-completo-infraestructura.md"

$outputs += Write-MarkdownBundle `
    -Title "Frontend completo" `
    -Description "Codigo completo del frontend web (`mechanic-web`)." `
    -Files $frontendFiles `
    -OutputFileName "frontend-completo.md"

$outputs += Write-MarkdownBundle `
    -Title "Mobile completo" `
    -Description "Codigo completo de la aplicacion mobile (`mechanic_mobile`)." `
    -Files $mobileFiles `
    -OutputFileName "mobile-completo.md"

$outputs += Write-MarkdownBundle `
    -Title "Env, YAML y configuracion" `
    -Description "Archivos de entorno, YAML/YML y configuracion general encontrados en el repositorio." `
    -Files $configFiles `
    -OutputFileName "env-yaml-configuracion.md"

Write-Host "Markdown generados en: $OutputPath"
foreach ($output in $outputs) {
    Write-Host "- $(Convert-ToRelativePath $output)"
}
