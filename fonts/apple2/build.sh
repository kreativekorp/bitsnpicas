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

rm -f PrintChar21.ttf PRNumber3.ttf

$BITSNPICAS convertbitmap -o PrintChar21.ttf -f ttf -p apple2-40col AppleII.kbits
$BITSNPICAS convertbitmap -o PRNumber3.ttf -f ttf -p apple2-80col AppleII.kbits
