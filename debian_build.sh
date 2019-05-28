#!/bin/bash

# Function to build dynamically debian "install" file
build_debian_install_file(){
    INSTALL=${VERSION_DIR}/debian/install
    rm ${INSTALL}

    # First : executable, desktop, icon, jar
    echo "liboffsearch /usr/bin" >> ${INSTALL}
    echo "liboffsearch.desktop /usr/share/applications" >> ${INSTALL}
    echo "liboffsearch.png /usr/share/pixmaps" >> ${INSTALL}
    echo "liboffsearch-${VERSION}.jar /usr/lib/liboffsearch" >> ${INSTALL}

    SOURCE_DIR_LENGTH=${#SOURCE_DIR}+1

	for f in `find ${SOURCE_DIR}/resources -name '*' -type f`
	do
	    # LEFT_PART = relative source file path with file name
	    LEFT_PART=${f:${#SOURCE_DIR}+1}
	    # RIGHT_PART = relative destination file path without file name
	    RIGHT_PART=`dirname $f`
	    RIGHT_PART=${RIGHT_PART:${SOURCE_DIR_LENGTH}}
	    echo "${LEFT_PART} /usr/lib/liboffsearch/${RIGHT_PART}" >> ${INSTALL}
	done
	# Remove lines related to .gitignore
    sed -i '/.gitignore/d' ${INSTALL}
}

# Transform config.properties for debian ppa packaging configuration
build_config_properties() {
    rm ${VERSION_DIR}/resources/config.properties
    
    # Read source config.properties and change paths of exec units
    while read line
    do
        output="${line}"
        # if line begins by exec, replace relative path with full path
        if [ ${#line} -ge 5 ] 
        then
            if [ "${line:0:4}" == "exec" ]
            then
                EQINDEX=`expr index "${line}" =`
                output=${line:0:${EQINDEX}}"/usr/lib/liboffsearch/"${line:${EQINDEX}}
            fi
        fi
        echo ${output} >> ${VERSION_DIR}/resources/config.properties             
    done < ${SOURCE_DIR}/resources/config.properties
}

# Build jar with maven
build_jar() {

     cd ${SOURCE_DIR}
     mvn clean install
     cd -  
     cp ${SOURCE_DIR}/target/liboffsearch-${VERSION}.jar ${VERSION_DIR}
}


### MAIN
echo "==Check arguments and init variables"

# Check arguments
if [ $# -ne 1 ]
then
  echo "$0 version"
  exit 1
fi
VERSION=${1}
PPA_DIR=~/Dev/ppa/liboffsearch
VERSION_DIR="${PPA_DIR}/liboffsearch_${VERSION}"
SOURCE_DIR=~/Dev/workspace2e/liboffsearch

echo "==Check pom.xml release consistency"
POM_RELEASE=`grep -m1 "version" ${SOURCE_DIR}/pom.xml|grep ${VERSION}`
if [ ${#POM_RELEASE} -eq 0 ]
then
    echo "pom release inconsistent with version passed in argument"
    exit 1
else
    echo "pom release is ${VERSION}"  
fi

echo "==Check changelog release consistency"
CHANGELOG_RELEASE=`grep -m1 "liboffsearch" ${SOURCE_DIR}/debian/changelog|grep ${VERSION}`
if [ ${#CHANGELOG_RELEASE} -eq 0 ]
then
    echo "changelog release inconsistent with version passed in argument"
    exit 1
else
    echo "pom release is ${VERSION}"  
fi

echo "==Create version directory"
rm -rf ${VERSION_DIR}
mkdir ${VERSION_DIR}

echo "==Manage executable file and desktop file and icon"
sed 's/<#VERSION#>/'"$VERSION"'/g' ${SOURCE_DIR}/liboffsearch > ${VERSION_DIR}/liboffsearch
chmod 755 ${VERSION_DIR}/liboffsearch
sed 's/<#VERSION#>/'"$VERSION"'/g' ${SOURCE_DIR}/liboffsearch.desktop > ${VERSION_DIR}/liboffsearch.desktop
cp ${SOURCE_DIR}/liboffsearch.png ${VERSION_DIR}

echo "==Copy debian static installation files"
cp -r ${SOURCE_DIR}/debian ${VERSION_DIR}

echo "==Build debian <install> file"
build_debian_install_file 

echo "==Copy resources files"
cp -r ${SOURCE_DIR}/resources ${VERSION_DIR}

echo "==Manage config.properties file"
build_config_properties

# Check changelog
echo "==Build jar and copy (maven)"
build_jar

# Check dependencies in control file
echo "==Logging dependencies"
cat ${VERSION_DIR}/debian/control | grep "Depends"

# source + pom ?
echo "==Copy source files"
cp ${SOURCE_DIR}/pom.xml ${VERSION_DIR}/.
cp -r ${SOURCE_DIR}/src ${VERSION_DIR}/.
# Clean gitignore files as they crash build on ppa
find ${VERSION_DIR} -name ".gitignore" -exec rm '{}' \;

# debuild -s ?
echo "==Build debian package"
cd ${VERSION_DIR}
debuild -S

# upload ?
echo "==To upload : "
echo "cd ${VERSION_DIR}/.."
echo "dput ppa:ladeche/ldec liboffsearch_${VERSION}_source.changes"
  