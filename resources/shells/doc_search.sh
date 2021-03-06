#!/bin/bash

# Bash to search for doc files, convert to txt, search for keyword 

# Check arguments
if [ $# -ne 3 ]; then
        echo "Usage: ${0} rootpath ext keyword"
        exit 1
else
    rootpath="${1}"
    ext="*.${2}"
    keyword="${3}"
fi
    
# Go to rootpath
cd "${rootpath}"

# Find files and loop on files 
find . -name "${ext}" -print | while read file
do	
    #echo "Found : "${file}
    # build unique target file name for pdftotext tool
    suffix=`date +"%Y%m%d%H%M%S%3N"`
    target="/tmp/docxls_search_${suffix}"
    
    # convert doc to temporary textfile
	catdoc "${file}" >${target}
    
    # search into text file
    nb=`grep -ic "${keyword}" ${target}`
    
    # if keyword found return file name
    if [ $nb -gt 0 ]; then
        echo ${file} 
        #echo $nb
    fi
    
    # remove temporary text file
    rm ${target}
done
