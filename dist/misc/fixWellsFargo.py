# Python program to fix up the Wells Fargo bad QFX export (missing end of line)
# hleofxquotes@gmail.com
# Usage:
# python fixWellsFargo.py  -i Checking1.qfx -o out.qfx
#
from __future__ import print_function   # If code has to work in Python 2 and 3!

import sys, getopt

def fixFile(inputFile, outputFile):
  file = open(inputFile, "r")
  print ('Reading from', inputFile)
  header=True

  headerList = []
  bodyList = []

  # read intput and split it into two lists: header and body
  while 1:
    char = file.read(1)
    if not char: break

    if header:
      if char == '<':
        header = False
        bodyList.append(char)
      else:
        headerList.append(char)
    else:
      if char == '<':
        bodyList.append('\n')
      bodyList.append(char)

  file.close()

  # fix up the header list
  headerKeys = [
    "DATA:",
    "VERSION:",
    "SECURITY:",
    "ENCODING:",
    "CHARSET:",
    "COMPRESSION:",
    "OLDFILEUID:",
    "NEWFILEUID:",
  ]
  s = "".join(headerList)
  for headerKey in headerKeys:
    s = s.replace(headerKey, "\n" + headerKey)
  fixedHeaders = s

  # body list already is good to go
  fixedBody = "".join(bodyList)

  # write output
  print ('Writing to', outputFile)
  file = open(outputFile, "w")
  file.write(fixedHeaders)
  file.write("\n")
  file.write("\n")
  file.write(fixedBody)
  file.write("\n")
  file.close()

def usage():
  print ('fixWellsFargo.py -i <inputFile> -o <outputFile>')

def main(argv):
  inputFile = ''
  outputFile = ''

  try:
    opts, args = getopt.getopt(argv,"hi:o:",["ifile=","ofile="])
  except getopt.GetoptError:
    usage()
    sys.exit(2)
  for opt, arg in opts:
    if opt == '-h':
      usage()
      sys.exit()
    elif opt in ("-i", "--ifile"):
      inputFile = arg
    elif opt in ("-o", "--ofile"):
      outputFile = arg

  if not inputFile:
    usage()
    sys.exit(2)
  if not outputFile:
    usage()
    sys.exit(2)

  fixFile(inputFile, outputFile)

if __name__ == "__main__":
  main(sys.argv[1:])

