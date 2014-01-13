# Using ImageMagick 6.7.6-9 2012-05-12 Q16 http://www.imagemagick.org
# To add more supported formats, simply add string in supportedFormats

import os
import re

while 1:
	try:
		directory = raw_input("Directory name (from current): ")
		file_names = [x[2] for x in os.walk(directory)][0]
	except:
		print "%s not a valid directory" % directory
		continue
	break

print "found %d files in %s" % (len(file_names),directory)

resolutions = ["drawable","drawable-ldpi","drawable-mdpi","drawable-hdpi","drawable-xhdpi","drawable-xxhdpi"]
supportedFormats = ["bmp","eps","png","svg"]

while 1:
		outFormat = raw_input("Choose format for output image (%s): " % ",".join(supportedFormats))
		if outFormat in supportedFormats:
			break

for res in resolutions:
	while 1:
		size = raw_input("Image size for %s, e.g. 64x64 (0x0 to skip this resolution): " % res)
		isValid = re.search("[0-9]+x[0-9]+", size)
		if isValid:
			break
		else:
			print "Invalid size"

	if (size == "0x0"):
		continue

	os.system("mkdir %s" % res);
	for inFile in file_names:
		split = inFile.rsplit(".",1)
		fileEnding = split[-1]
		outFile = split[0]
		if fileEnding and not fileEnding in supportedFormats:
			print ("%s not supported, skipping" % inFile)
			continue
		os.system("convert ./%s/%s -background none -resize %s ./%s/%s.%s" % (directory,inFile, size, res, outFile, outFormat))



#holmhansen$ convert s13.svg -background none -resize 224x312x out.png