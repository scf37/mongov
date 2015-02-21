<#function url path>
    <#return requestContext.pathToServlet + path>
</#function>

<#macro css src>
	<link rel="stylesheet" href="${url(src) + '?' + appVersion()}" type="text/css">
</#macro>

<#macro script src>
<script type="text/javascript" src="${url(src) + '?' + appVersion()}"></script> 
</#macro>

<#function tri b v1 v2>
	<#if b>
		<#return v1>
	<#else>
		<#return v2>
	</#if>
	<#return 2>
</#function>


<#function bi b v>
	<#if b>
		<#return v>
	<#else>
		<#return ''>
	</#if>
</#function>

<#macro join list on>
	<#list list as item>${item}<#if item_has_next>${on}</#if> </#list>
</#macro>

<#assign appendParam = "ru.scf37.web.freemarker.AppendUrlParamFunction"?new()>
<#assign removeParam = "ru.scf37.web.freemarker.RemoveUrlParamFunction"?new()>  
<#assign setParam = "ru.scf37.web.freemarker.SetUrlParamFunction"?new()>
<#assign appVersion = "ru.scf37.web.freemarker.VersionFunction"?new()>
