function prep() {
	var ua = navigator.userAgent || navigator.appVersion;
	var mac = (ua.indexOf('Macintosh') >= 0) || (ua.indexOf('Mac OS') >= 0) || (ua.indexOf('MacOS') >= 0);
	var win = (ua.indexOf('Windows') >= 0);
	var os = (mac ? 'mac' : (win ? 'win' : 'linux'));
	var e = document.getElementsByClassName(os);
	for (var i = 0; i < e.length; i++) {
		e[i].className = e[i].className.replace('hidden', '');
	}
}
prep();
