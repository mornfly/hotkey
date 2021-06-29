$("#form-add").validate({
	submitHandler:function(form){
		add();
	}
});

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
		let token = getCookie("token");
		if(XMLHttpRequest.status == 1000 && ( token == "undefined" || token =="")){
			top.location.href = '/user/login';
		}
	},
	success : function(data) {
		console.log(data)
		let role = data.role;
		if(role === "ADMIN"){
			$("#apps").val("");
		}
		let apps = data.appNames;
		let appName = data.appName;
		for (let i = 0; i < apps.length; i++) {
			let app = apps[i];
			if(app === appName){
				$("#apps").val(apps[i]);
			}
		}
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

