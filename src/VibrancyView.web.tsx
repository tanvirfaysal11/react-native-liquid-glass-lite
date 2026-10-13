// Web implementation of VibrancyView.
import React from 'react';
import type { VibrancyViewProps } from './VibrancyView';
import BlurView from './BlurView.web';

/**
 * Web implementation of VibrancyView.
 *
 * iOS uses UIVibrancyEffect, which has no web equivalent, so this delegates to
 * BlurView — the same strategy as the native Android fallback.
 */
export const VibrancyView: React.FC<VibrancyViewProps> = ({
  blurType = 'xlight',
  blurAmount = 10,
  style,
  children,
  ...rest
}) => {
  return (
    <BlurView
      blurType={blurType}
      blurAmount={blurAmount}
      style={style}
      {...rest}
    >
      {children}
    </BlurView>
  );
};

export default VibrancyView;
