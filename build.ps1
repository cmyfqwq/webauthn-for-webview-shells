param()
$ErrorActionPreference = 'Stop'
$dir = 'C:\Users\ZhuanZ\Downloads\qwq\webauthn-for-webview-shells'
$sdk = "$env:LOCALAPPDATA\Android\Sdk"
$bt = "$sdk\build-tools\35.0.0"
$androidJar = "$sdk\platforms\android-36\android.jar"
$d8Jar = "$sdk\build-tools\35.0.0\lib\d8.jar"
$build = "$dir\build"

Remove-Item "$dir\build" -Recurse -Force -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force "$build\classes" | Out-Null
Write-Host '[1/6] javac'
& javac --release 11 -encoding UTF-8 -sourcepath "$dir\src" -cp "$dir\deps\webkit-1.17-classes.jar;$dir\deps\kotlin-stdlib.jar;$androidJar" -d "$build\classes" (Get-ChildItem "$dir\src" -Recurse -Filter *.java | Select-Object -ExpandProperty FullName)
if ($LASTEXITCODE) { throw "javac failed" }
Write-Host '[2/6] prog.jar'
Push-Location "$build\classes"; try { jar cf "$build\prog.jar" io } finally { Pop-Location }
Write-Host '[3/6] d8'
& java "-cp" "$d8Jar" com.android.tools.r8.D8 --release --min-api 24 --output "$build\dexout.zip" "$build\prog.jar" "$dir\deps\webkit-1.17-classes.jar" "$dir\deps\kotlin-stdlib.jar"
if ($LASTEXITCODE) { throw "d8 failed" }
New-Item -ItemType Directory -Force "$build\dex" | Out-Null
tar -xf "$build\dexout.zip" -C "$build\dex"
Write-Host '[4/6] aapt2 + zip assembly'
& "$bt\aapt2.exe" link -o "$build\base.apk" -I "$androidJar" --manifest "$dir\AndroidManifest.xml" --min-sdk-version 24 --target-sdk-version 34
if ($LASTEXITCODE) { throw "aapt2 failed" }
$stage = "$build\stage"
Remove-Item $stage -Recurse -Force -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force "$stage\assets" | Out-Null
tar -xf "$build\base.apk" -C $stage
Copy-Item "$dir\assets\xposed_init" "$stage\assets\xposed_init" -Force
Get-ChildItem "$build\dex" -Filter *.dex | Copy-Item -Destination $stage -Force
Push-Location $stage
try {
  $dexList = (Get-ChildItem . -Filter *.dex | Select-Object -ExpandProperty Name)
  tar --format zip -cf packed.apk AndroidManifest.xml $dexList 'assets'
} finally { Pop-Location }
Write-Host '[5/6] zipalign'
& "$bt\zipalign.exe" -p -f 4 "$stage\packed.apk" "$build\aligned.apk"
if ($LASTEXITCODE) { throw "zipalign failed" }
Write-Host '[6/6] sign'
$ks = "$dir\module.ks"
if (-not (Test-Path $ks)) { & keytool -genkeypair -keystore $ks -storetype PKCS12 -storepass passkeywar -keypass passkeywar -dname 'CN=WebAuthnShell' -alias dsh -keyalg RSA -keysize 2048 -validity 10000 | Out-Null }
& "$bt\apksigner.bat" sign --ks $ks --ks-key-alias dsh --ks-pass pass:passkeywar --key-pass pass:passkeywar --out "$build\WebAuthn-Shell-1.0.0.apk" "$build\aligned.apk"
if ($LASTEXITCODE) { throw "apksigner failed" }
Write-Host ("OK -> $build\WebAuthn-Shell-1.0.0.apk  size=" + (Get-Item "$build\WebAuthn-Shell-1.0.0.apk").Length)
