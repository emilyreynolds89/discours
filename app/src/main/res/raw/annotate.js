console.log("annotate.js LOADED");

var body = document.querySelector('body');

body.addEventListener('dblclick', function(e) {
    console.log("DOUBLE CLIKED AT: X=" + e.pageX + " Y=" + e.pageY);
})