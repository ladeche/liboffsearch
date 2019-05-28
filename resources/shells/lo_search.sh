#!/bin/bash
if [ $# -ne 3 ]; then
        echo "Usage: lo_search rootpath ext keyword"
        exit 1
else
    rootpath="${1}"
    ext="*.${2}"
    keyword="${3}"
fi
    
cd "${rootpath}"

find . -name "${ext}" -print | while read file
do	
    #echo "Found : "${file}
    nb=`unzip -ca "$file" content.xml | grep -ic "${keyword}"`
    
    if [ $nb -gt 0 ]; then
        echo $file 
        #$nb
    fi
done
