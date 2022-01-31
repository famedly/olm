#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html.
# Run `pod lib lint flutter_olm.podspec' to validate before publishing.
#
Pod::Spec.new do |s|

  # The libolm version
  MAJOR = ?
  MINOR = ?
  PATCH = ?

  s.name             = 'flutter_olm'
  s.version          = "#{MAJOR}.#{MINOR}.#{PATCH}"
  s.summary          = 'Flutter plugin to include olm (http://matrix.org/git/olm)'
  s.description      = <<-DESC
                                       olm is an implementation of the Double Ratchet cryptographic ratchet in C++
                       DESC
  s.homepage         = 'https://gitlab.com/famedly/libraries/olm'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Famedly GmbH' => 'info@famedly.com' }
  s.source           = { :path => '.' }

  s.source_files = 'Classes/**/*', 'include/**/*.{h,hh}', 'src/*.{c,cpp}', 'lib/crypto-algorithms/sha256.c', 'lib/crypto-algorithms/aes.c', 'lib/curve25519-donna/curve25519-donna.c'
  s.public_header_files = 'Classes/**/*.h'
  # Those files (including .c) are included by ed25519.c. We do not want to compile them twice
  s.preserve_paths = "lib/ed25519/**/*.{h,c}"
  s.library = 'c++'
  s.compiler_flags = "-g -O3 -DOLMLIB_VERSION_MAJOR=#{MAJOR} -DOLMLIB_VERSION_MINOR=#{MINOR} -DOLMLIB_VERSION_PATCH=#{PATCH}"
  # For headers search paths, manage first the normal installation. Then, use paths used
  # when the pod is local
  s.xcconfig = {
    'USER_HEADER_SEARCH_PATHS' =>"${PODS_ROOT}/OLMKit/include ${PODS_ROOT}/OLMKit/lib #{File.join(File.dirname(__FILE__), 'include')} #{File.join(File.dirname(__FILE__), 'lib')}"
  }

  s.dependency 'Flutter'
  s.platform = :ios, '8.0'

  # Flutter.framework does not contain a i386 slice. Only x86_64 simulators are supported.
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES', 'VALID_ARCHS[sdk=iphonesimulator*]' => 'x86_64' }
end
