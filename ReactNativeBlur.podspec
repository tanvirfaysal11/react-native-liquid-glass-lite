require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "ReactNativeBlur"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = { "Tanvir Faysal" => "t.faysal45@gmail.com" }

  # The SwiftUI-backed views require iOS 13+, so never resolve below that even
  # when the host app's `min_ios_version_supported` is older.
  s.platforms    = { :ios => [min_ios_version_supported.to_f, 13.0].max }
  s.source       = { :path => "." }

  s.source_files = "ios/**/*.{h,m,mm,cpp,swift}"
  s.swift_version = "5.0"
  s.module_name  = "ReactNativeBlur"
  s.private_header_files = "ios/**/*.h"

  install_modules_dependencies(s)
end
