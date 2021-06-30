$("#form-add").validate({
	submitHandler:function(form){
		add();
	}
});

function add() {
	var dataFormJson=$("#form-add").serialize();
	$.ajax({
		cache : true,
		type : "POST",
		url : "/key/add",
		data : dataFormJson,
		headers: {
			"Authorization":getCookie("token")
		},
		async : false,
		error : function(XMLHttpRequest){
			$.modal.alertError(XMLHttpRequest.responseJSON.msg);
		},
		success : function(data) {
			$.operate.saveSuccess(data);
		}
	});
}


function init() {

	console.log(1);
	$.ajax({
		cache : true,
		type : "POST",
		url : "/user/info",
		headers: {
			"Authorization":getCookie("token")
		},
		async : false,
		error : function(XMLHttpRequest){
			$.modal.alertError(XMLHttpRequest.responseJSON.msg);
		},
		success : function(data) {
			console.log(data)
			let role = data.role;
			if(role === "ADMIN"){
				$("#apps").append("<option></option>");
			}
			let apps = data.appNames;
			let appName = data.appName;
			for (let i = 0; i < apps.length; i++) {
				let app = apps[i];
				if(app === appName){
					$("#apps").append("<option selected = selected>" + apps[i] + "</option>");
				}else{
					$("#apps").append("<option>" + apps[i] + "</option>");
				}
			}
		}
	});
}

$(function(){
	init();
})


