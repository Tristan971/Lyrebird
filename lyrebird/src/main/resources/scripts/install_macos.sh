#!/bin/sh

#
#    Lyrebird, a free open-source cross-platform twitter client.
#    Copyright (C) 2017-2018, Tristan Deloche
#
#    This program is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with this program.  If not, see <http://www.gnu.org/licenses/>.
#
#
#    This script is based off of Michele & Ksenia Balistreri's work.
#    Original is here : <https://github.com/bitgamma/updatefx/blob/3c9d2e31712dd8a832417917fd976e04542a04c6/src/main/resources/com/briksoftware/updatefx/util/installdmg.sh>
#
#

DMG_FILE="$1"
TMP_NAME="lyrebird-update-$(date +%s)"

echo "Lyrebird macOS update script starting!"
echo "Will update to version in DMG file : ${DMG_FILE}"

echo "Converting ${DMG_FILE} file to CDR/UDTO at position : /tmp/${TMP_NAME}.cdr"
/usr/bin/hdiutil convert -quiet "$DMG_FILE" -format UDTO -o /tmp/${TMP_NAME}

echo "Attaching /tmp/${TMP_NAME}"
/usr/bin/hdiutil attach -quiet -nobrowse -noautoopen -readonly -mountpoint /Volumes/${TMP_NAME} /tmp/${TMP_NAME}.cdr

shopt -s nullglob

pushd /Volumes/${TMP_NAME} > /dev/null
for app in *.app; do
  echo "Deleting /Applications/$app"
  /bin/rm -rf "/Applications/$app"
  echo "Copying $app to /Applications"
  /bin/cp -pR "/Volumes/$TMP_NAME/$app" /Applications/
done
popd > /dev/null

echo "Detaching disk image..."
/usr/bin/hdiutil detach -quiet /Volumes/${TMP_NAME}

echo "Deleting /tmp/${TMP_NAME}.cdr"
/bin/rm /tmp/${TMP_NAME}.cdr
/bin/rm "$DMG_FILE"
