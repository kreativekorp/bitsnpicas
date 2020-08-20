#!/usr/bin/env python

import os
import re
import shutil
import sys


def getlayout(evdev, name):
	with open(evdev, 'r') as f:
		contents = f.read()
		pattern = re.compile(r'^\s+<layout>\s+<configItem>\s+<name>' + name + r'</name>.+?</layout>\n', re.M | re.S)
		match = pattern.search(contents)
		if match:
			return match.group(0)
		else:
			return None


def rmlayout(evdev, name):
	with open(evdev, 'r') as f:
		contents = f.read()
		pattern = re.compile(r'^\s+<layout>\s+<configItem>\s+<name>' + name + r'</name>.+?</layout>\n', re.M | re.S)
		return pattern.sub('', contents)


def addlayout(evdev, layout):
	with open(evdev, 'r') as f:
		contents = f.read()
		pattern = re.compile(r'^\s+</layoutList>\n', re.M)
		def repl(m):
			return layout + m.group(0)
		return pattern.sub(repl, contents)


def rmifexists(path):
	if os.path.exists(path):
		os.remove(path)


def cpmkdir(src, dst):
	if os.path.exists(src):
		try:
			shutil.copy(src, dst)
		except (IOError, OSError):
			os.makedirs(os.path.dirname(dst))
			shutil.copy(src, dst)


def doremove(sysEvdev, localEvdev, sysSymbols, localSymbols, name, dispName):
	try:
		contents = rmlayout(sysEvdev, name)
		with open(sysEvdev, 'w') as f:
			f.write(contents)
		rmifexists(os.path.join(sysSymbols, name))
		rmifexists(os.path.join(sysSymbols, name + '.png'))
		print('Successfully removed %s.' % dispName)
	except (IOError, OSError) as err:
		print('Could not remove %s: %s' % (dispName, err))
		print('(Try running as root.)')


def doinstall(sysEvdev, localEvdev, sysSymbols, localSymbols, name, dispName):
	try:
		cpmkdir(os.path.join(localSymbols, name), os.path.join(sysSymbols, name))
		cpmkdir(os.path.join(localSymbols, name + '.png'), os.path.join(sysSymbols, name + '.png'))
		layout = getlayout(localEvdev, name)
		contents = addlayout(sysEvdev, layout)
		with open(sysEvdev, 'w') as f:
			f.write(contents)
		print('Successfully installed %s.' % dispName)
	except (IOError, OSError) as err:
		print('Could not install %s: %s' % (dispName, err))
		print('(Try running as root.)')


def dointeractive(sysEvdev, localEvdev, sysSymbols, localSymbols, name, dispName):
	if getlayout(sysEvdev, name):
		print('The %s layout is currently installed.' % dispName)
		print('Remove it? [y/N]')
		a = sys.stdin.readline().strip().lower()
		if a == 'y' or a == 'yes':
			doremove(sysEvdev, localEvdev, sysSymbols, localSymbols, name, dispName)
	else:
		print('The %s layout is currently NOT installed.' % dispName)
		print('Install it? [Y/n]')
		a = sys.stdin.readline().strip().lower()
		if a != 'n' and a != 'no':
			doinstall(sysEvdev, localEvdev, sysSymbols, localSymbols, name, dispName)


def doargv(sysEvdev, localEvdev, sysSymbols, localSymbols, name, dispName):
	if len(sys.argv) < 2:
		dointeractive(sysEvdev, localEvdev, sysSymbols, localSymbols, name, dispName)
	elif len(sys.argv) == 2:
		if sys.argv[1].lower() == 'check':
			if getlayout(sysEvdev, name):
				print('The %s layout is currently installed.' % dispName)
			else:
				print('The %s layout is currently NOT installed.' % dispName)
		elif sys.argv[1].lower() == 'install':
			doinstall(sysEvdev, localEvdev, sysSymbols, localSymbols, name, dispName)
		elif sys.argv[1].lower() == 'remove':
			doremove(sysEvdev, localEvdev, sysSymbols, localSymbols, name, dispName)
		else:
			print('Usage: %s [check|install|remove]' % sys.argv[0])
	else:
		print('Usage: %s [check|install|remove]' % sys.argv[0])


def main(name, dispName):
	doargv('/usr/share/X11/xkb/rules/evdev.xml', 'evdev.xml', '/usr/share/X11/xkb/symbols', '.', name, dispName)
