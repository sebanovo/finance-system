type RgbColor = {
  r: number;
  g: number;
  b: number;
};

type OklabColor = {
  l: number;
  a: number;
  b: number;
};

type OklchColor = {
  l: number;
  c: number;
  h: number;
};

function clamp(value: number, min: number, max: number): number {
  return Math.min(Math.max(value, min), max);
}

function round(value: number, decimals: number): number {
  const factor = 10 ** decimals;
  return Math.round(value * factor) / factor;
}

function normalizeHex(hex: string): string {
  const value = hex.trim().replace("#", "");

  if (value.length === 3) {
    return value
      .split("")
      .map(char => char + char)
      .join("");
  }

  return value;
}

function hexToRgb(hex: string): RgbColor {
  const normalized = normalizeHex(hex);

  if (!/^[0-9a-fA-F]{6}$/.test(normalized)) {
    throw new Error(`Invalid hex color: ${hex}`);
  }

  const r = parseInt(normalized.slice(0, 2), 16);
  const g = parseInt(normalized.slice(2, 4), 16);
  const b = parseInt(normalized.slice(4, 6), 16);

  return { r, g, b };
}

function srgbChannelToLinear(channel: number): number {
  const value = channel / 255;

  if (value <= 0.04045) {
    return value / 12.92;
  }

  return ((value + 0.055) / 1.055) ** 2.4;
}

function rgbToOklab(rgb: RgbColor): OklabColor {
  const r = srgbChannelToLinear(rgb.r);
  const g = srgbChannelToLinear(rgb.g);
  const b = srgbChannelToLinear(rgb.b);

  const l = 0.4122214708 * r + 0.5363325363 * g + 0.0514459929 * b;
  const m = 0.2119034982 * r + 0.6806995451 * g + 0.1073969566 * b;
  const s = 0.0883024619 * r + 0.2817188376 * g + 0.6299787005 * b;

  const lRoot = Math.cbrt(l);
  const mRoot = Math.cbrt(m);
  const sRoot = Math.cbrt(s);

  return {
    l: 0.2104542553 * lRoot + 0.793617785 * mRoot - 0.0040720468 * sRoot,
    a: 1.9779984951 * lRoot - 2.428592205 * mRoot + 0.4505937099 * sRoot,
    b: 0.0259040371 * lRoot + 0.7827717662 * mRoot - 0.808675766 * sRoot,
  };
}

function oklabToOklch(oklab: OklabColor): OklchColor {
  const c = Math.sqrt(oklab.a ** 2 + oklab.b ** 2);
  let h = (Math.atan2(oklab.b, oklab.a) * 180) / Math.PI;

  if (h < 0) {
    h += 360;
  }

  return {
    l: clamp(oklab.l, 0, 1),
    c: Math.max(0, c),
    h,
  };
}

export function hexToOklch(hex: string): OklchColor {
  const rgb = hexToRgb(hex);
  const oklab = rgbToOklab(rgb);

  return oklabToOklch(oklab);
}

export function hexToOklchCss(hex: string): string {
  const color = hexToOklch(hex);

  return `oklch(${round(color.l, 4)} ${round(color.c, 4)} ${round(color.h, 2)})`;
}
