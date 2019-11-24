#!/usr/bin/env bash

if test -f BitsNPicas.jar; then
	BITSNPICAS="java -jar BitsNPicas.jar"
elif test -f ../BitsNPicas/BitsNPicas.jar; then
	BITSNPICAS="java -jar ../BitsNPicas/BitsNPicas.jar"
elif test -f ../Workspace/BitsNPicas/BitsNPicas.jar; then
	BITSNPICAS="java -jar ../Workspace/BitsNPicas/BitsNPicas.jar"
elif test -f ../../BitsNPicas/BitsNPicas.jar; then
	BITSNPICAS="java -jar ../../BitsNPicas/BitsNPicas.jar"
elif test -f ../../Workspace/BitsNPicas/BitsNPicas.jar; then
	BITSNPICAS="java -jar ../../Workspace/BitsNPicas/BitsNPicas.jar"
elif test -f ../../../BitsNPicas/BitsNPicas.jar; then
	BITSNPICAS="java -jar ../../../BitsNPicas/BitsNPicas.jar"
elif test -f ../../../Workspace/BitsNPicas/BitsNPicas.jar; then
	BITSNPICAS="java -jar ../../../Workspace/BitsNPicas/BitsNPicas.jar"
elif test -f ../../../../BitsNPicas/BitsNPicas.jar; then
	BITSNPICAS="java -jar ../../../../BitsNPicas/BitsNPicas.jar"
elif test -f ../../../../Workspace/BitsNPicas/BitsNPicas.jar; then
	BITSNPICAS="java -jar ../../../../Workspace/BitsNPicas/BitsNPicas.jar"
elif test -f ../../../../../BitsNPicas/BitsNPicas.jar; then
	BITSNPICAS="java -jar ../../../../../BitsNPicas/BitsNPicas.jar"
elif test -f ../../../../../Workspace/BitsNPicas/BitsNPicas.jar; then
	BITSNPICAS="java -jar ../../../../../Workspace/BitsNPicas/BitsNPicas.jar"
elif test -f ../../../../../../BitsNPicas/BitsNPicas.jar; then
	BITSNPICAS="java -jar ../../../../../../BitsNPicas/BitsNPicas.jar"
elif test -f ../../../../../../Workspace/BitsNPicas/BitsNPicas.jar; then
	BITSNPICAS="java -jar ../../../../../../Workspace/BitsNPicas/BitsNPicas.jar"
else
	echo "Could not find BitsNPicas."
	exit 1
fi

rm -f *.ttf

$BITSNPICAS convertbitmap -o PetMe.ttf -f ttf PetMe.kbits
$BITSNPICAS convertbitmap -o PetMe2X.ttf -f ttf -p commodore-20col PetMe.kbits
$BITSNPICAS convertbitmap -o PetMe2Y.ttf -f ttf -p commodore-80col PetMe.kbits
$BITSNPICAS convertbitmap -o PetMe64.ttf -f ttf PetMe64.kbits
$BITSNPICAS convertbitmap -o PetMe642Y.ttf -f ttf -p commodore-80col PetMe64.kbits
$BITSNPICAS convertbitmap -o PetMe128.ttf -f ttf PetMe128.kbits
$BITSNPICAS convertbitmap -o PetMe1282Y.ttf -f ttf -p commodore-80col PetMe128.kbits

$BITSNPICAS convertbitmap -o Berkelium64.ttf -f ttf Berkelium64.kbits
$BITSNPICAS convertbitmap -o Berkelium1541.ttf -f ttf Berkelium1541.kbits
$BITSNPICAS convertbitmap -o Giana.ttf -f ttf Giana.kbits
