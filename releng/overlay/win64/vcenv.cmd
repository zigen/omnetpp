@echo off
rem Specify the location of your Visual Studio installation
set VS_INSTALL_DIR=C:\Program Files (x86)\Microsoft Visual Studio\2019\BuildTools

rem Specify the installation directory of the clang compiler.
set ClangToolsInstallDir=%VS_INSTALL_DIR%\VC\Tools\Llvm

rem Specify Visual Studio batch file to start a developer console for 64-bit host/target builds.
set VCVARS64=%VS_INSTALL_DIR%\VC\Auxiliary\Build\vcvars64.bat

rem *** Do not change anything below this line ***
set HOME=%~dp0
set OMNETPP_ROOT=%HOME%
set OMNETPP_IMAGE_PATH=.\images;%OMNETPP_ROOT%\images

cd "%OMNETPP_ROOT%\tools"
IF EXIST "opp-tools-win64-msys.7z" (
cls
echo.
echo We need to unpack the MinGW toolchain before continuing.
echo This can take a while, please be patient.
echo.
pause
7za x -aos -y opp-tools-win64-msys.7z
del opp-tools-win64-msys.7z
)

IF EXIST "opp-tools-win64-extra.7z" (
7za x -aos -y opp-tools-win64-extra.7z
del opp-tools-win64-extra.7z
)

IF EXIST "opp-tools-win64-visualc.7z" (
cls
echo.
echo We need to unpack the libraries required by the ms-clang toolchain before continuing.
echo This can take a while, please be patient.
echo.
pause
7za x -aos -y opp-tools-win64-visualc.7z
del opp-tools-win64-visualc.7z

"win64\visualc\bin\qtbinpatcher.exe" --qt-dir=win64\visualc
echo *** Files for Microsoft Clang toolchain extracted. ***
)

cd "%OMNETPP_ROOT%"

rem check for the MS Visual C++ installation
if NOT EXIST "%VCVARS64%" goto MESSAGE_STUDIO_MISSING
if NOT EXIST "%ClangToolsInstallDir%" goto MESSAGE_CLANG_MISSING

call "%VCVARS64%"

rem Delete local variables so child process will not inherit it unnecessarily
set VS_INSTALL_DIR=
rem Extra path required to build OMNET. Save the current path in a variable so it can be added
rem to the path in the msys shell. This will made the MS compilers available in the msys shell.
set __EXTRA_PATH=%OMNETPP_ROOT%\tools\win64\visualc\bin;%ClangToolsInstallDir%\bin;%VCToolsInstallDir%\bin\HostX64\x64

rem Add some OMNET specific directories so tools will be available from the Windows CMD shell, too.
rem This is not needed for the MSYS shell because the PATH variable is overwritten by the MSYS shell.
set PATH=%__EXTRA_PATH%;%PATH%;%OMNETPP_ROOT%\bin;%OMNETPP_ROOT%\tools\win64\usr\bin;%OMNETPP_ROOT%\tools\win64\mingw64\bin

rem Open the MinGW command shell (you may add -full-path to force the MSYS shell to inherit the current system path)
if "%1" == "ide" (
rem if the first paramter is "ide" we start the IDE instead of the shell. This can be used to start the IDE from a shortcut
call "%OMNETPP_ROOT%\bin\omnetpp.cmd"
) else (
call "%OMNETPP_ROOT%\tools\win64\msys2_shell.cmd" -mingw64
)

goto EOF

:MESSAGE_STUDIO_MISSING
echo You must specify the the location of your Visual Studio installation.
echo.
echo Open 'vcenv.cmd' with your editor and set the VS_INSTALL_DIR
echo variable to point to your installation. You should also check
echo the 'ClangToolsInstallDir' variable and verify if the version
echo number at the end of the path is matching your installation.
goto EOF

:MESSAGE_CLANG_MISSING
echo You must install the Clang compiler and then specify its location.
echo.
echo Open 'vcenv.cmd' with your editor and set the ClangToolsInstallDir
echo variable to point to your installation.

:EOF
