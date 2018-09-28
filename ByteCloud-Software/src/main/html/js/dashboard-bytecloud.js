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