#!/bin/bash

# Find proper ok
for pdfpgm in okular mupdf evince ; do
    pgminst=`command -v ${pdfpgm}`
    if [ ${#pgminst} -gt 0 ]
    then
    	${pgminst} "${1}" 
    fi 
done 

