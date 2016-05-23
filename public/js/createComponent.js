var getUrlParamHtml = "<div class='col-sm-12'><div class='col-sm-2'><input required='true' type='text' class='form-control' id='formKey' name='urlParamKey' placeholder='form variable key'></div><div class='col-sm-4'><input required='true' type='text' class='form-control' id='formParamValue' name='urlParamValue' placeholder='Value'></div><div class='col-sm-1'><a onclick='removeThisParam(this)' href='javascript:void(0);' class='btn btn-link btn-xs' id='removeParam' on><span class='glyphicon glyphicon-remove'></span></a></div></div>";
var getUNPWDHtml = "<div class='col-sm-2'><input required='true' type='text' class='form-control' id='formKey' name='basicAuthUserKey' placeholder='user name'></div><div class='col-sm-2'><input required='true' type='text' class='form-control' id='formParamValue' name='basicAuthPwd' placeholder='password'></div><div class='col-sm-1'><a onclick='removeThisParam(this)' href='javascript:void(0);' class='btn btn-link btn-xs' id='removeParam' on><span class='glyphicon glyphicon-remove'></span></a></div>";
var getFormParamHtml = "<div class='col-sm-12'><div class='col-sm-2'><input required='true' type='text' class='form-control' id='formKey' name='paramKey' placeholder='form variable key'></div><div class='col-sm-4'><input required='true' type='text' class='form-control' id='formParamValue' name='paramValue' placeholder='Value'></div><div class='col-sm-1'><a onclick='removeThisParam(this)' href='javascript:void(0);' class='btn btn-link btn-xs' id='removeParam' on><span class='glyphicon glyphicon-remove'></span></a></div></div>";
var getHeaderHtml = "<div class='col-sm-12'><div class='col-sm-2'><input required='true' type='text' class='form-control' id='headerKey' name='headerKey' placeholder='Header variable key'></div><div class='col-sm-4'><input required='true' type='text' class='form-control' id='headerValue' name='headerValue' placeholder='Value'></div><div class='col-sm-1'><a onclick='removeThisParam(this)' href='javascript:void(0);' class='btn btn-link btn-xs' id='removeParam' on><span class='glyphicon glyphicon-remove'></span></a></div></div>";
var getJsonHtml = "<div class='col-sm-12'><div class='col-sm-6'><textarea required='true' type='text' class='form-control' id='headerKey' name='jsonBody' placeholder='Header variable key'>{}</textarea></div><div class='col-sm-1'><a onclick='removeThisParam(this)' href='javascript:void(0);' class='btn btn-link btn-xs' id='removeParam' on><span class='glyphicon glyphicon-remove'></span></a></div></div>";
var getPostAddParamHtml = "<div class='col-sm-12'><div class='col-sm-2'><input required='true' type='text' class='form-control' id='headerKey' name='headerKey' placeholder='Header variable key'></div><div class='col-sm-4'><input required='true' type='text' class='form-control' id='headerValue' name='headerValue' placeholder='Value'></div><div class='col-sm-1'><a onclick='removeThisParam(this)' href='javascript:void(0);' class='btn btn-link btn-xs' id='removeParam' on><span class='glyphicon glyphicon-remove'></span></a></div></div>";

function getVariableHtml(selectedText) {
	return "<div class='col-sm-12'><div class='col-sm-5'><input name='resJsonPropVal' class='form-control' placeholder='property value' value='"
			+ selectedText
			+ "'></div><div class='col-sm-5'><input name='resJsonPropVarName' class='form-control' placeholder='variable name'></div><div class='col-sm-1'><a onclick='removeThisParam(this)' href='javascript:void(0);' class='btn btn-link btn-xs' id='removeParam' on><span class='glyphicon glyphicon-remove'></span></a></div></div>";
}

function responseDo(typeOfMethod, reqId) {
	$('#myPleaseWait').modal('show');
	var responseData = $("#resData-" + reqId);
	if (typeOfMethod == 'format') {
		var jsObj = JSON.parse(responseData.text());
		$("#formatedResponse-" + reqId).jJsonViewer(jsObj);

	} else if (typeOfMethod == 'addval') {
		console.log("selected text: " + $.selection());
		$("#keysResponse-" + reqId).append(getVariableHtml($.selection()));
	}
	$('#myPleaseWait').modal('hide');
}

$("#reqType").change(function() {
	if ($("#reqType").val() == "POST") {
		$("#reqBodyWrapper").show();
		$("#noOfGetParamsWrapper").hide();
		$("#paramsWrapper").hide();
	} else if ($("#reqType").val() == "GET") {
		$("#reqBodyWrapper").hide();
		$("#noOfGetParamsWrapper").show();
		$("#paramsWrapper").show();
	} else {
		$("#reqBodyWrapper").hide();
		$("#noOfGetParamsWrapper").hide();
		$("#paramsWrapper").hide();
	}
});

$(document).ready(function() {
	$("#reqBodyWrapper").hide();
});
function hideMeReqType(linkTo) {
	var parent = $(linkTo).parent().parent().parent();
	parent.children(".tab-content").fadeToggle(300, function() {
		var spanArrow = $(linkTo).find("span");
		if (spanArrow.hasClass('glyphicon-chevron-down')) {
			spanArrow.removeClass('glyphicon-chevron-down');
			spanArrow.addClass('glyphicon-chevron-right');
		} else if ($(linkTo).find("span").hasClass('glyphicon-chevron-right')) {
			spanArrow.removeClass('glyphicon-chevron-right');
			spanArrow.addClass('glyphicon-chevron-down');
		}
	});
}
function addElement(typeOfParam, linkTo) {
	$('#myPleaseWait').modal('show');
	console.log('in method');
	var parent = $(linkTo).parent().parent();
	if (typeOfParam == 'headerparam') {
		parent.append(getHeaderHtml);
	} else if (typeOfParam == 'urlparam') {
		parent.append(getUrlParamHtml);
	} else if (typeOfParam == 'formparam') {
		parent.append(getFormParamHtml);
	} else if (typeOfParam == 'jsonparam') {
		parent.append(getJsonHtml);
	} else if (typeOfParam == 'removeReqType') {
		console.log('else: ' + typeOfParam);
		// $(linkTo).parent().parent().parent().css("background", "yellow");
		$(linkTo).parent().parent().parent().remove();
	} else {
		console.log('else: ' + typeOfParam);
	}
	$('#myPleaseWait').modal('hide');
	return false;
}
function removeElement(testcaseId, reqId) {
	$('#myPleaseWait').modal('show');
	$.get("/remove-unit-request/"  + reqId, function(data,
			status) {
		$("#unit-request-" + reqId).remove();
		$('#myPleaseWait').modal('hide');
		$("#add-http-get-component").removeClass('disabled');
		$("#add-http-post-component").removeClass('disabled');
	}
, "html");
	
}
function addRequestFields(requestType) {
	var addRequestTypeList = requestType;
	var testcaseId = $("#testCaseid").val();
	// alert(addRequestTypeList);
	var requestChainView = $("#request-chain-view");
	$.get("/get-unit-request/" + addRequestTypeList + "/" + testcaseId,
			function(data, status) {
				requestChainView.append(data);
			});
}

function addRequestFieldsToComp(requestType) {
	var addRequestTypeList = requestType;
	var testcaseId = "NA";
	// alert(addRequestTypeList);
	var requestChainView = $("#request-chain-view");
	$.get("/get-unit-request/" + addRequestTypeList + "/" + testcaseId,
			function(data, status) {
				$("#add-http-get-component").addClass('disabled');
				$("#add-http-post-component").addClass('disabled');
				requestChainView.html(data);
			});

}

function addBasicAuth(reqId) {
	$("#basicAuth-" + reqId).html(getUNPWDHtml);
}

function saveBasicRequest(typeOfReq, reqId) {
	$('#myPleaseWait').modal('show');
	$.ajax({
		url : '/save-unit-request',
		type : 'POST',
		data : $("#unit-request-" + reqId).serialize(),
		dataType : 'json',
		success : function(result) {
			console.log("saved: "+reqId);
			$('#myPleaseWait').modal('hide');
		},
		error : function(xhr, error) {
			console.log("error: "+xhr.responseText);
			$('#myPleaseWait').modal('hide');
		}
	});
}

function saveTestCase(typeOfReq, reqId) {
	 if(typeOfReq=='TESTCASE'){
			$("#testCaseid").val();
			$('[name="basicRequestIdForReq"]').each(function(index) {
				console.log("saving..."+$(this).val());
				saveBasicRequest('',$(this).val());
			});
			}else if(typeOfReq=='TC&RUN'){
			$('[name="basicRequestIdForReq"]').each(function(index) {
				console.log("saving..."+$(this).val());
				saveBasicRequest('',$(this).val());
				$('#myPleaseWait').modal('hide');
			});
			$.get("/test/" + $("#testCaseid").val(), function(data,
					status) {
				showOverLay('Sample Run Response',data);
			}
		, "html");
		}
}

function testBasicRequest(typeOfReq, reqId) {
	$('#myPleaseWait').modal('show');
	console.log('submiting form' + "#unit-request-" + reqId)
	console.log('test req...' + $("#unit-request-" + reqId).serialize());
	$.ajax({
		url : '/test-unit-request/' + typeOfReq,
		type : 'POST',
		data : $("#unit-request-" + reqId).serialize(),
		dataType : 'json',
		success : function(result) {
			// $("#responseWrapper").attr("class","col-md-5 alert
			// alert-success");
			var resType = $("#responseType-" + reqId).val();
			if (resType == 'jsonres') {
				$("#resViewer-astext-" + reqId).hide();
				var jsObj = JSON.parse(result.RESPONSEBODY);
				$("#formatedResponse-" + reqId).jJsonViewer(jsObj);
				$("#resViewer-asjson-" + reqId).show();
			} else if (resType == 'textres') {
				$("#resViewer-asjson-" + reqId).hide();
				$("#resCode-" + reqId).val(result.STATUSCODE);
				$("#resData-" + reqId).text(result.RESPONSEBODY);
				$("#resViewer-astext-" + reqId).show();
			}
			$('a[href="#request-' + reqId + '"]').first().parent().removeClass(
					'active');
			$('a[href="#resViewer-' + reqId + '"]').first().parent().addClass(
					'active');
			$("#resViewer-" + reqId).addClass('active in');
			$("#request-" + reqId).removeClass('active in');

		},
		error : function(xhr, error) {
			alert(xhr.responseText);
		}
	});
	$('#myPleaseWait').modal('hide');
}

function addHeaderFields() {
	// alert($("#reqType").val());
	if ($("#reqType").val() == "GET") {
		$("#headersWrapper").append(getHeaderHtml);
	}
}
function addParamFields() {
	// alert($("#reqType").val());
	if ($("#reqType").val() == "GET") {
		$("#paramsWrapper").append(getParamText);
	}
}
function removeThisParam(obj) {
	// alert("r u sure?");
	$(obj).parent().parent().remove();
}

function showOverLay(header,body){
	$('#showOverLay').modal('show');
	$('#overlay-header').html(header);
	$('#overlay-body').html(body);
	//$('#showOverLay').modal('hide');

	
}