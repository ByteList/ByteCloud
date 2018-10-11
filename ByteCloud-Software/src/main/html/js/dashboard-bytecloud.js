let cloudTerminal;

function updateMonitor(url) {
    this.setInterval(function() {
        $.get(url, function(data) {
            data = data.split(":");
            $('#sys-cpu-load').css({
                width: data[0]+"%"
            }).html(data[0]+"%");
            $('#sys-ram-load').css({
                width: data[1]+"%"
            }).html(data[1]+"%");
            $('#storage').css({
                width: data[2]+"%"
            }).html(data[2]+"%");
        });
    }, 2000);
}

function initTerminal(socket) {
    $("#console").terminal(function(command) {
            if (command !== '') {

            } else {
                this.echo('');
            }
        }, {
            name: 'cloud',
            height: 500,
            prompt: '> ',
            greetings: null,
            onInit: function (terminal) {
                cloudTerminal = terminal;
                terminal.echo(
                    "   ____        _        _____ _                 _ \n" +
                    "  |  _ \\      | |      / ____| |               | |\n" +
                    "  | |_) |_   _| |_ ___| |    | | ___  _   _  __| |\n" +
                    "  |  _ <| | | | __/ _ \\ |    | |/ _ \\| | | |/ _` |\n" +
                    "  | |_) | |_| | ||  __/ |____| | (_) | |_| | (_| |\n" +
                    "  |____/ \\__, |\\__\\___|\\_____|_|\\___/ \\__,_|\\__,_|\n" +
                    "          __/ | T I G E R\n" +
                    "         |___/                 b y   B y t e L i s t\n\n\n" +
                    "  W e b - T e r m i n a l  -  Alpha v0.1\n" +
                    "\n");
                terminal.echo("Connecting to cloud...");
                connect(socket);
            }
        }
    );
}

function connect(socket) {
    const connection = new WebSocket(socket, ['soap', 'xmpp']);
    connection.send("");
    connection.onmessage = function (e) {
        terminal.echo(e.data);
    }
}