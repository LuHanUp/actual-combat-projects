$(document).ajaxSend(function (event, request, settings) {
    if (settings.type === 'POST') {
        let sourceData = settings.data;
        if (sourceData !== undefined && sourceData !== '') {
            let data = {};
            let split = sourceData.split("&");
            for (const index in split) {
                let strings = split[index].split("=");
                data[strings[0]] = strings[1];
            }
            settings.data = JSON.stringify(data);
            settings.contentType = "application/json;charset=UTF-8";
            request.setRequestHeader("Content-type", "application/json;charset=UTF-8");
        }
    }
    console.log("event:", event,);
    console.log("request:", request);
    console.log("settings:", settings);
});

const ResponseUtils = {
    isSuccess: function (data) {
        if (data.code === 200) return true;
        else return false;
    },
};
