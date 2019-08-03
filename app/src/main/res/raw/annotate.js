console.log("annotate.js LOADED");

var body = document.querySelector('body');

body.addEventListener('dblclick', function(e) {
    console.log("DOUBLE CLIKED AT: X=" + e.pageX + " Y=" + e.pageY);
    Android.sendCoords(e.pageX, e.pageY);
})

function drawAnnotation(positionX, positionY, text, username) {
    var body = document.querySelector('body');
    console.log("Trying to annotate: " + positionX + " " + positionY + " " + text + " " + username);
    var div = document.createElement("div");
    var textContent = document.createTextNode("@" + username + ": " + text);

    div.appendChild(textContent);

    div.class = "annotation";
    div.style.cssText = "position: absolute; background-color: yellow; height: 60px; width: 120px; z-index: 1;";
    div.style.top = positionY;
    div.style.left = positionX;

    body.appendChild(div);
}