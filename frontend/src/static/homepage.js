console.log("Hello from homepage.js!");
fetch(`${API_ROOT}time`)
    .then(response => response.json())
    .then(json => $("#time").text(json.human));

$("#reverseButton").click(ev => {
    ev.preventDefault();
    const textToReverse = $("#textToReverse").val();
    $("#reversedResult").text("<loading>")
    fetch(`${API_ROOT}reverse`, { method: "POST", body: JSON.stringify({ payload: textToReverse }) })
        .then(response => response.json())
        .then(json => {
            console.log(json);
            $("#reversedResult").text(json.payload ?? json.message)
        });
});