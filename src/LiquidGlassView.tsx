import React from 'react';
import { Platform } from 'react-native';
import type { StyleProp, ViewStyle } from 'react-native';
import ReactNativeLiquidGlassView, {
  type GlassType,
} from './ReactNativeLiquidGlassViewNativeComponent';
import BlurView from './BlurView';

export interface LiquidGlassViewProps {
  /**
   * The UIKit glass effect variant.
   * @default 'clear'
   * @platform iOS 26+
   */
  glassType?: GlassType;

  /**
   * Tint color overlaid on the glass surface. Accepts hex strings (`'#RRGGBB'`)
   * or `'clear'` for no tint.
   * @default 'clear'
   * @platform iOS 26+
   */
  glassTintColor?: string;

  /**
   * Opacity of the glass tint layer (0–1).
   * @default 1.0
   * @platform iOS 26+
   */
  glassOpacity?: number;

  /**
   * Fallback color shown when reduced transparency is enabled or on
   * unsupported OS versions.
   * @default '#FFFFFF'
   */
  reducedTransparencyFallbackColor?: string;

  /**
   * Whether touch events pass through to content behind the view.
   * @default true
   * @platform iOS
   */
  isInteractive?: boolean;

  /**
   * Expand behind safe-area insets.
   * @default false
   * @platform iOS
   */
  ignoreSafeArea?: boolean;

  style?: StyleProp<ViewStyle>;
  children?: React.ReactNode;
}

/**
 * Renders an iOS 26+ UIKit Liquid Glass effect.
 *
 * On iOS < 26 and Android the component falls back to a standard `BlurView`
 * with the tint color applied as an overlay, so layouts remain intact across
 * all OS versions.
 *
 * @example
 * ```tsx
 * <LiquidGlassView glassType="clear" glassTintColor="#007AFF" glassOpacity={0.8}>
 *   <Text>Hello</Text>
 * </LiquidGlassView>
 * ```
 */
export const LiquidGlassView: React.FC<LiquidGlassViewProps> = ({
  glassType = 'clear',
  glassTintColor = 'clear',
  glassOpacity = 1.0,
  reducedTransparencyFallbackColor = '#FFFFFF',
  isInteractive = true,
  ignoreSafeArea = false,
  style,
  children,
  ...rest
}) => {
  const iosVersion =
    Platform.OS === 'ios' ? Number.parseInt(String(Platform.Version), 10) : 0;
  const supportsLiquidGlass = Platform.OS === 'ios' && iosVersion >= 26;

  if (!supportsLiquidGlass) {
    // Build an overlay color string that includes the opacity channel.
    // Only append alpha when glassTintColor is a hex string.
    const tintWithAlpha =
      glassTintColor !== 'clear' && glassTintColor.startsWith('#')
        ? glassTintColor +
          Math.round(glassOpacity * 255)
            .toString(16)
            .padStart(2, '0')
        : undefined;

    return (
      <BlurView
        blurAmount={70}
        overlayColor={tintWithAlpha}
        reducedTransparencyFallbackColor={reducedTransparencyFallbackColor}
        ignoreSafeArea={ignoreSafeArea}
        style={style}
      >
        {children}
      </BlurView>
    );
  }

  return (
    <ReactNativeLiquidGlassView
      glassType={glassType}
      glassTintColor={glassTintColor}
      glassOpacity={glassOpacity}
      reducedTransparencyFallbackColor={reducedTransparencyFallbackColor}
      isInteractive={isInteractive}
      ignoreSafeArea={ignoreSafeArea}
      style={style}
      {...rest}
    >
      {children}
    </ReactNativeLiquidGlassView>
  );
};

export default LiquidGlassView;
