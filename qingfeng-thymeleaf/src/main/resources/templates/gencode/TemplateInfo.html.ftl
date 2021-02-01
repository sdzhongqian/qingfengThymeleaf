<!DOCTYPE html>
<html lang="zh" xmlns:th="http://www.thymeleaf.org">
<head>
    <th:block th:include="/web/system/admin/include :: top"></th:block>
</head>
<body class="gray-bg">

<form class="layui-form" action="" id="form">
    <table class="layui-table" style="width:95%;margin:10px auto;">
    <#if tablePd.temp_type == '2'>
        <tr>
            <td width="20%" align="right"><label class="layui-form-label">父级名称：<span style="color: red">*</span></label></td>
            <td colspan="3">
                <th:block th:if="${'$'}{p[parent_name]?:''==''}">
                    <span th:text="${tablePd.menu_name}"></span>
                </th:block>
                <th:block th:if="${'$'}{p[parent_name]?:''!=''}">
                    <span th:text="${'$'}{p[parent_name]?:'' }"></span>
                </th:block>
            </td>
        </tr>
    </#if>
<#list fieldList as obj>
    <#if obj.field_operat == 'Y'>
    <#if obj.show_type == '1'||obj.show_type == '7'>
        <tr>
            <td width="15%" align="right">
                <label class="layui-form-label">${obj.field_comment}：
                    <#if obj.verify_rule?contains("required")>
                        <span style="color: red">*</span>
                    </#if>
                </label>
            </td>
            <td colspan="3" th:text="${'$'}{p.${obj.field_name}}"></td>
        </tr>
    </#if>
    <#if obj.show_type == '2'>
        <tr>
            <td width="15%" align="right">
                <label class="layui-form-label">${obj.field_comment}：
                    <#if obj.verify_rule?contains("required")>
                        <span style="color: red">*</span>
                    </#if>
                </label>
            </td>
            <td colspan="3" th:text="${'$'}{p.${obj.field_name}}"></td>
        </tr>
    </#if>
    <#if obj.show_type == '3'>
        <tr>
            <td width="15%" align="right">
                <label class="layui-form-label">${obj.field_comment}：
                    <#if obj.verify_rule?contains("required")>
                        <span style="color: red">*</span>
                    </#if>
                </label>
            </td>
            <td colspan="3">
                <div id="${obj.field_name}">
                <#if obj.option_content?contains(";")>
                    <#list obj.option_content?split(";") as name>
                    <#assign param = name?split("/")>
                    <th:block th:if="${'$'}{p.${obj.field_name}=='${param[0]}'}">
                        ${param[1]}
                    </th:block>
                    </#list>
                </#if>
                </div>
            </td>
        </tr>
    </#if>
    <#if obj.show_type == '4'>
        <tr>
            <td width="15%" align="right">
                <label class="layui-form-label">${obj.field_comment}：
                    <#if obj.verify_rule?contains("required")>
                        <span style="color: red">*</span>
                    </#if>
                </label>
            </td>
            <td colspan="3">
                <div id="${obj.field_name}">
                <#if obj.option_content?contains(";")>
                    <#list obj.option_content?split(";") as name>
                    <#assign param = name?split("/")>
                    <th:block th:if="${'$'}{p.${obj.field_name}=='${param[0]}'}">
                        ${param[1]}
                    </th:block>
                    </#list>
                </#if>
                </div>
            </td>
        </tr>
    </#if>
    <#if obj.show_type == '5'>
        <tr>
            <td width="15%" align="right">
                <label class="layui-form-label">${obj.field_comment}：
                    <#if obj.verify_rule?contains("required")>
                        <span style="color: red">*</span>
                    </#if>
                </label>
            </td>
            <td colspan="3">
                <div id="${obj.field_name}">
                <#if obj.option_content?contains(";")>
                    <#list obj.option_content?split(";") as name>
                    <#assign param = name?split("/")>
                    <th:block th:if="${'$'}{p.${obj.field_name}.indexOf('${param[0]}')!=-1}">
                        ${param[1]}
                    </th:block>
                    </#list>
                </#if>
                </div>
            </td>
        </tr>
    </#if>
    <#if obj.show_type == '6'>
        <#assign richText = 'true'>
        <tr>
            <td width="15%" align="right">
                <label class="layui-form-label">${obj.field_comment}：
                    <#if obj.verify_rule?contains("required")>
                        <span style="color: red">*</span>
                    </#if>
                </label>
            </td>
            <td colspan="3" th:text="${'$'}{p.${obj.field_name}}">
            </td>
        </tr>
    </#if>
    <#if obj.show_type == '8'>
        <tr>
            <td width="15%" align="right">
                <label class="layui-form-label">${obj.field_comment}：
                    <#if obj.verify_rule?contains("required")>
                        <span style="color: red">*</span>
                    </#if>
                </label>
            </td>
            <td colspan="3">
                <div style="margin-top:5px;">
                    <table class="layui-table">
                        <thead>
                        <tr>
                            <th>附件名称</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody id="tbody_file_${obj.field_name}">
                        <th:block th:each="v,vs:${'$'}{${obj.field_name}FileList}">
                            <tr th:id="'tr_ls'+${'$'}{v.id}">
                                <td th:text="${'$'}{v.name}"></td>
                                <td>
                                    <div class="layui-btn-group">
                                        <button type="button" th:onclick="downloadFile([[${'$'}{v.id}]],[[${'$'}{v.file_path}]],[[${'$'}{v.name}]]);" class="layui-btn layui-btn-xs">下载</button>
                                    </div>
                                </td>
                            </tr>
                        </th:block>
                        </tbody>
                    </table>
                </div>
            </td>
        </tr>
    </#if>
</#if>
</#list>

    <#if tablePd.temp_type == '1'>
        <tr>
            <td colspan="4">
                <table class="layui-table" style="width:95%;margin:10px auto;">
                    <thead>
                        <#list linkFieldList as obj>
                            <#if obj.field_operat == 'Y' && obj.show_type != '0'>
                            <th>${obj.field_comment}<#if obj.verify_rule?contains("required")><span style="color: red">*</span></#if></th>
                            </#if>
                        </#list>
                    </thead>
                    <tbody id="child_table">
                    <th:block th:if="${'$'}{list.size()>0}">
                        <th:block th:each="var,status:${'$'}{list}">
                            <tr th:id="'child_tr_ls'+${'$'}{var.id}" style="height: 36px;">
                            <#list linkFieldList as obj>
                            <#if obj.field_operat == 'Y'>
                                <#if obj.show_type == '1'||obj.show_type == '7'>
                                <td th:text="${'$'}{var.${obj.field_name}}"></td>
                                </#if>
                                <#if obj.show_type == '2'||obj.show_type == '6'>
                                <td th:text="${'$'}{var.${obj.field_name}}"></td>
                                </#if>
                                <#if obj.show_type == '3'>
                                <td>
                                    <div id="${obj.field_name}_${'$'}{var.id}">
                                        <#if obj.option_content?contains(";")>
                                            <#list obj.option_content?split(";") as name>
                                                <#assign param = name?split("/")>
                                                <th:block th:if="${'$'}{var.${obj.field_name}=='${param[0]}'}">
                                                ${param[1]}
                                                </th:block>
                                            </#list>
                                        </#if>
                                    </div>
                                </td>
                                </#if>
                                <#if obj.show_type == '4'>
                                <td>
                                    <div id="${obj.field_name}_${'$'}{var.id}">
                                        <#if obj.option_content?contains(";")>
                                            <#list obj.option_content?split(";") as name>
                                                <#assign param = name?split("/")>
                                                <th:block th:if="${'$'}{var.${obj.field_name}=='${param[0]}'}">
                                                ${param[1]}
                                                </th:block>
                                            </#list>
                                        </#if>
                                    </div>
                                </td>
                                </#if>
                                <#if obj.show_type == '5'>
                                <td>
                                    <div id="${obj.field_name}_${'$'}{var.id}">
                                        <#if obj.option_content?contains(";")>
                                            <#list obj.option_content?split(";") as name>
                                                <#assign param = name?split("/")>
                                                <th:block th:if="${'$'}{var.${obj.field_name}.indexOf('${param[0]}')!=-1}">
                                                ${param[1]}
                                                </th:block>
                                            </#list>
                                        </#if>
                                    </div>
                                </td>
                                </#if>
                                <#if obj.show_type == '8'>
                                <td>
                                    <div style="margin-top:5px;">
                                        <table class="layui-table">
                                            <thead>
                                            <tr>
                                                <th>附件名称</th>
                                                <th>操作</th>
                                            </tr>
                                            </thead>
                                            <tbody th:id="'tbody_file_${obj.field_name}_'+${'$'}{var.id}">
                                            <th:block th:each="v,vs:${'$'}{var.${obj.field_name}FileList}">
                                                <tr th:id="'tr_ls'+${'$'}{v.id}">
                                                    <td th:text="${'$'}{v.name}"></td>
                                                    <td>
                                                        <div class="layui-btn-group">
                                                            <button type="button" th:onclick="downloadFile([[${'$'}{v.id}]],[[${'$'}{v.file_path}]],[[${'$'}{v.name}]]);" class="layui-btn layui-btn-xs">下载</button>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </th:block>
                                            </tbody>
                                        </table>
                                    </div>
                                </td>
                                </#if>
                            </#if>
                            </#list>
                            </tr>
                        </th:block>
                    </th:block>
                    </tbody>
                </table>
            </td>
        </tr>
    </#if>
        <tr>
            <td style="text-align: center; padding-top: 10px;" colspan="4">
                <div class="layui-form-item">
                    <button type="button" class="layui-btn layui-btn-danger layui-btn-sm" id="cancel">关闭</button>
                </div>
            </td>
        </tr>
    </table>
</form>

<script type="text/javascript" th:src="@{/static/plugins/xadmin/lib/layui/layui.js}" charset="utf-8"></script>
<script type="text/javascript" th:src="@{/static/js/qfverify.js}"></script>
<script type="text/javascript" th:src="@{/static/js/qfAjaxReq.js}"></script>
<script type="text/javascript" th:src="@{/static/js/uploadFile.js}"></script>
<!-- 注意：如果你直接复制所有代码到本地，上述js路径需要改成你本地的 -->
<script>
    var form,layer,layedit,laydate,upload;
    layui.use(['form', 'layedit', 'laydate','upload'], function(){
        form = layui.form;
        layer = layui.layer;
        layedit = layui.layedit;
        laydate = layui.laydate;
        upload = layui.upload;

        ${'$'}('#cancel').on('click',function (){
            var index = parent.layer.getFrameIndex(window.name);
            parent.layer.close(index);
        })

        //初始化
<#list fieldList as obj>
    <#if obj.field_operat == 'Y'>
    <#if obj.show_type == '3'>
        <#if !obj.option_content?contains(";")>
        findValueDictionary('${obj.field_name}','[[${'$'}{p.${obj.field_name}}]]');
        </#if>
    </#if>
    <#if obj.show_type == '4'>
        <#if !obj.option_content?contains(";")>
        findValueDictionary('${obj.field_name}','[[${'$'}{p.${obj.field_name}}]]');
        </#if>
    </#if>
    <#if obj.show_type == '5'>
        <#if !obj.option_content?contains(";")>
        findValueDictionary('${obj.field_name}','[[${'$'}{p.${obj.field_name}}]]');
        </#if>
    </#if>
</#if>
</#list>

<#if tablePd.temp_type == '1'>
    <#list linkFieldList as obj>
        <#if obj.field_operat == 'Y'>
            <#if obj.show_type == '3'>
                <#if !obj.option_content?contains(";")>
                ${'$'}.each(${'$'}{listJson},function(i,n){
                    findValueDictionary('${obj.field_name}_'+ n.id,'[[${'$'}{p.${obj.field_name}}]]');
                })
                </#if>
            </#if>
            <#if obj.show_type == '4'>
                <#if !obj.option_content?contains(";")>
                ${'$'}.each(${'$'}{listJson},function(i,n){
                    findRadioDictionary('${obj.option_content}_'+ n.id,'div_'+ n.id+'_','${obj.field_name}_'+ n.id,'${obj.default_value}');
                })
                </#if>
            </#if>
            <#if obj.show_type == '5'>
                <#assign checkboxText = 'true'>
                <#if !obj.option_content?contains(";")>
                ${'$'}.each(${'$'}{listJson},function(i,n){
                    findCheckboxDictionary('${obj.option_content}_'+ n.id,'div-'+ n.id+'-','${obj.field_name}_'+ n.id,'${obj.default_value}');
                })
                </#if>
            </#if>
        </#if>
    </#list>

</#if>
    });
</script>

<th:block th:include="/web/system/admin/include :: bottom" />
</body>
</html>