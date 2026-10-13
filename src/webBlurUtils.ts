// Web blur utilities (CSS backdrop-filter).
import { useSyncExternalStore } from 'react';
import type { ViewStyle } from 'react-native';
import type { BlurType } from './ReactNativeBlurViewNativeComponent';
import type { ProgressiveBlurDirection } from './ReactNativeProgressiveBlurViewNativeComponent';

/**
 * ViewStyle extended with the web-only CSS properties the web implementations
 * rely on. react-native-web forwards unknown style keys to the DOM, but inline
 * styles are not auto-prefixed, so the Webkit twins must always be set
 * alongside the unprefixed properties.
 */
export type WebBlurStyle = ViewStyle & {
  backdropFilter?: string;
  WebkitBackdropFilter?: string;
  maskImage?: string;
  WebkitMaskImage?: string;
  backgroundImage?: string;
};

/** Scale from the 0-100 `blurAmount` prop to a CSS blur radius in px. */
export const WEB_BLUR_RADIUS_SCALE = 0.4;

/** Number of stacked backdrop-filter layers used for the progressive ramp. */
export const PROGRESSIVE_LAYER_COUNT = 5;

/**
 * Stacked backdrop-filters compound as roughly the root-sum-of-squares of their
 * radii, so radii are scaled down slightly to keep the compound peak matching
 * `blurAmount`.
 */
export const PROGRESSIVE_RADIUS_COMPENSATION = 0.87;

/** Maps each native blur type to the semi-transparent tint it produces on iOS. */
export const BLUR_TYPE_TO_BACKGROUND: Record<BlurType, string> = {
  xlight: 'rgba(255, 255, 255, 0.6)',
  light: 'rgba(255, 255, 255, 0.4)',
  dark: 'rgba(0, 0, 0, 0.5)',
  extraDark: 'rgba(0, 0, 0, 0.75)',
  regular: 'rgba(255, 255, 255, 0.2)',
  prominent: 'rgba(255, 255, 255, 0.35)',

  systemUltraThinMaterial: 'rgba(255, 255, 255, 0.1)',
  systemThinMaterial: 'rgba(255, 255, 255, 0.2)',
  systemMaterial: 'rgba(255, 255, 255, 0.3)',
  systemThickMaterial: 'rgba(255, 255, 255, 0.45)',
  systemChromeMaterial: 'rgba(255, 255, 255, 0.5)',

  systemUltraThinMaterialLight: 'rgba(255, 255, 255, 0.15)',
  systemThinMaterialLight: 'rgba(255, 255, 255, 0.25)',
  systemMaterialLight: 'rgba(255, 255, 255, 0.4)',
  systemThickMaterialLight: 'rgba(255, 255, 255, 0.55)',
  systemChromeMaterialLight: 'rgba(255, 255, 255, 0.6)',

  systemUltraThinMaterialDark: 'rgba(0, 0, 0, 0.2)',
  systemThinMaterialDark: 'rgba(0, 0, 0, 0.3)',
  systemMaterialDark: 'rgba(0, 0, 0, 0.45)',
  systemThickMaterialDark: 'rgba(0, 0, 0, 0.6)',
  systemChromeMaterialDark: 'rgba(0, 0, 0, 0.7)',
};

const clamp = (value: number, min: number, max: number): number =>
  Math.min(Math.max(value, min), max);

/** Formats a number for CSS without float noise (2 decimals max). */
const cssNumber = (value: number): number => Number(value.toFixed(2));

let backdropFilterSupport: boolean | undefined;

/**
 * Feature-detects CSS `backdrop-filter`. Safe during server-side / static
 * rendering (there is no CSS API in Node) — it reports false there, leaving
 * the tint-only fallback. Memoized once the CSS API is available.
 */
export function supportsBackdropFilter(): boolean {
  if (backdropFilterSupport === undefined) {
    let css: { supports?: (property: string, value: string) => boolean } | undefined;
    try {
      // Reach the global object via indirect eval so this type-checks without
      // a DOM or ES2020 lib, and resolves in both browser and Node (SSR).
      const scope = (0, eval)('this') as {
        CSS?: { supports?: (property: string, value: string) => boolean };
      };
      css = scope.CSS;
    } catch {
      css = undefined;
    }
    backdropFilterSupport =
      typeof css?.supports === 'function' &&
      (css.supports('backdrop-filter', 'blur(1px)') ||
        css.supports('-webkit-backdrop-filter', 'blur(1px)'));
  }
  return backdropFilterSupport;
}

// Browser support does not change during a mounted component's lifetime.
const subscribeToBackdropSupport = () => () => {};
const serverBackdropSupport = () => false;

/** Server/hydration-safe read of backdrop-filter support. */
export function useBackdropFilterSupport(): boolean {
  return useSyncExternalStore(
    subscribeToBackdropSupport,
    supportsBackdropFilter,
    serverBackdropSupport
  );
}

/**
 * Style for a uniform blur layer: the blurType tint as background plus, when
 * supported, a backdrop blur scaled from the 0-100 `blurAmount`. Without
 * backdrop-filter support only the tint remains (graceful degradation).
 */
export function getBlurLayerStyle(
  blurType: BlurType,
  blurAmount: number,
  hasBackdropFilter = supportsBackdropFilter()
): WebBlurStyle {
  const style: WebBlurStyle = {
    backgroundColor:
      BLUR_TYPE_TO_BACKGROUND[blurType] ?? BLUR_TYPE_TO_BACKGROUND.regular,
  };
  if (hasBackdropFilter) {
    const radius = cssNumber(clamp(blurAmount, 0, 100) * WEB_BLUR_RADIUS_SCALE);
    const filter = `blur(${radius}px) saturate(180%)`;
    style.backdropFilter = filter;
    style.WebkitBackdropFilter = filter;
  }
  return style;
}

/** One layer of the stacked progressive blur. */
export interface ProgressiveLayer {
  /** Backdrop blur radius in px. */
  radius: number;
  /** CSS mask limiting where this layer's blur is visible. */
  maskImage: string;
}

const pct = (value: number): string => `${cssNumber(value)}%`;

const maskForLayer = (
  direction: ProgressiveBlurDirection,
  startOffset: number,
  layer: number
): string => {
  const n = PROGRESSIVE_LAYER_COUNT;

  if (direction === 'blurredCenterClearTopAndBottom') {
    const inset = Math.min(startOffset, 0.3);
    const q = (j: number) => 30 - inset * 100 + (20 * j) / n;
    const inner = q(n - layer);
    const outer = q(n - layer + 1);
    return (
      `linear-gradient(to bottom, transparent ${pct(50 - outer)}, ` +
      `black ${pct(50 - inner)}, black ${pct(50 + inner)}, ` +
      `transparent ${pct(50 + outer)})`
    );
  }

  const boundary = (j: number) =>
    startOffset * 100 + ((1 - startOffset) * 100 * j) / n;
  const axis = direction === 'blurredBottomClearTop' ? 'to top' : 'to bottom';
  const solidEnd = boundary(n - layer);
  const fadeEnd = boundary(n - layer + 1);
  return `linear-gradient(${axis}, black 0%, black ${pct(solidEnd)}, transparent ${pct(fadeEnd)})`;
};

/**
 * Stacked layers for a progressive blur: each covers the fully-blurred plateau
 * and fades one gradient band further than the next stronger one, halving the
 * radius per band. Composited, they produce a true blur-radius ramp from
 * `blurAmount` px at the blurred edge down to sharp at the clear edge.
 */
export function getProgressiveLayers(
  direction: ProgressiveBlurDirection,
  startOffset: number,
  blurAmount: number
): ProgressiveLayer[] {
  const s = clamp(startOffset, 0, 1);
  const maxRadius = Math.max(blurAmount, 0) * PROGRESSIVE_RADIUS_COMPENSATION;
  const layers: ProgressiveLayer[] = [];
  for (let layer = 1; layer <= PROGRESSIVE_LAYER_COUNT; layer++) {
    layers.push({
      radius: cssNumber(maxRadius / 2 ** (PROGRESSIVE_LAYER_COUNT - layer)),
      maskImage: maskForLayer(direction, s, layer),
    });
  }
  return layers;
}

/**
 * Gradient for the tint layer of a progressive blur: the blurType tint at full
 * strength over the blurred plateau, fading to transparent at the clear edge.
 */
export function getProgressiveTintGradient(
  direction: ProgressiveBlurDirection,
  startOffset: number,
  tint: string
): string {
  const s = clamp(startOffset, 0, 1);

  if (direction === 'blurredCenterClearTopAndBottom') {
    const inset = Math.min(s, 0.3) * 100;
    return (
      `linear-gradient(to bottom, transparent ${pct(inset)}, ${tint} ${pct(inset + 20)}, ` +
      `${tint} ${pct(80 - inset)}, transparent ${pct(100 - inset)})`
    );
  }

  const axis = direction === 'blurredBottomClearTop' ? 'to top' : 'to bottom';
  return `linear-gradient(${axis}, ${tint} 0%, ${tint} ${pct(s * 100)}, transparent 100%)`;
}
