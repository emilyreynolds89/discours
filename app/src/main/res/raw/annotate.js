console.log("annotate.js LOADED");
console.log("screen width: " + window.screen.width);

var body = document.querySelector('body');

body.addEventListener('dblclick', function(e) {
    console.log("DOUBLE CLIKED AT: X=" + e.pageX + " Y=" + e.pageY);
    Android.sendCoords(e.pageX, e.pageY, window.screen.width);
})

