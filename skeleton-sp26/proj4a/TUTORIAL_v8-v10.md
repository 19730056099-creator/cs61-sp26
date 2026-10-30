# 教程：从「当前版本」一步步改到第 10 版（v8 → v9 → v10）

> 本教程依据 `_old_versions/software2026_12(8)`、`(9)`、`(10)` 三份课堂代码，与项目根目录下**当前的代码**（v7 + 自己补完的编辑功能，尚未提交）逐文件对比后整理而成。
> 跟着下面的步骤改完，`src/` 下的代码会与 `software2026_12(10)` 完全一致（`application.properties` 除外，见第 0 步）。

## 总览

| 阶段 | 课堂主题 | 改动的文件 |
|---|---|---|
| 第 0 步 | 认清起点 | —— |
| 第 1 步 → v8 | 修改学生信息（编辑页 + 专业下拉框回显） | `HzieeStuMapper`、`HzieeStuService`、`WebHzieeStuController`、**新增** `show_hziee_stu_edit.html` |
| 第 2 步 → v9 | 按条件模糊查询 + 分页 | **新增** `HzieePageInfo`、`HzieeStuMapper`、`HzieeStuService`、`WebHzieeStuController`、**新增** `qry_hziee_stu_list.html` |
| 第 3 步 → v10 | 修正分页 SQL 的 `limit` 写法 | `HzieeStuMapper`（只改 1 行） |

数据流向始终是同一条链路，每加一个功能都按这个顺序从下往上改：

```
MySQL ← Mapper（写 SQL） ← Service（封装业务） ← Controller（接请求、放 Model） → templates/*.html（Thymeleaf 显示）
```

---

## 第 0 步：认清起点

当前代码 = v7 提交 + 你自己写的编辑功能（`git status` 能看到 4 个修改文件）：

- `HzieeStuMapper`：已经有 `getHzieeStuByNo`（按学号查）和 `updateHzieeStuInfo`（修改）。
- `HzieeStuService`：已经有同名的两个服务方法。
- `WebHzieeStuController`：`web_hziee_stu_edit` 已去掉误调用的删除；`web_hziee_stu_do_edit` 已调用修改。
- `application.properties`：密码改成了 `test123456`，驱动改成了 `com.mysql.cj.jdbc.Driver`。
- 还**没有** `show_hziee_stu_edit.html`，所以点「修改」会报模板找不到。

> **关于 `application.properties`**：老师的 v8~v10 里仍是 `password=123456` 和旧驱动 `com.mysql.jdbc.Driver`。这个文件**保持你自己的配置，不要改回去**，否则连不上你本机的数据库。下面所有步骤都不涉及它。

建议先提交一下当前进度，方便出错时回退：

```bash
git add -A
git commit -m "v7.5: 自己补完编辑功能"
```

---

## 第 1 步：升级到 v8 —— 修改学生信息

你自己的写法思路是对的，v8 是老师的标准写法，主要区别：

1. 方法改名：`getHzieeStuByNo` → `getAEditHzieeStuInfo`，`updateHzieeStuInfo` → `HzieeStuUpdate`。
2. 编辑页需要**专业下拉框**，所以 Controller 还要把专业列表放进 Model。
3. 提交修改的请求名从 `web_hziee_stu_do_edit` 改成 `web_hziee_stu_update`。
4. 修改失败时用 `RedirectAttributes` 带参数重定向（你的写法 `"...?hziee_stu_no"+学号` 少了一个 `=`，这正是一个 bug）。
5. 新增编辑页面 `show_hziee_stu_edit.html`。

### 1.1 改 Mapper：`mapper/HzieeStuMapper.java`

**① 删除**你写在 `getAHzieeStuInfo` 下面的这段：

```java
    //按学号查找一个学生
    @Select("select * from hziee_stu_info" +
            "   where hziee_stu_no=#{stu_no}    ")
    HzieeStuInfo getHzieeStuByNo(@Param("stu_no") int stu_no);
```

**② 删除**你写在 `addHzieeStuInfo` 下面的 `updateHzieeStuInfo` 整段（注释 + `@Update(...)` + 方法声明）。

**③ 在文件末尾**、`deleteHzieeStuNotTrue` 方法之后、最后一个 `}` 之前，加入：

```java
    @Select("select * from hziee_stu_info where  hziee_stu_no=#{stu_no}")
    HzieeStuInfo getAEditHzieeStuInfo(@Param("stu_no") int stu_no);

    @Update("update hziee_stu_info set hziee_stu_name=#{hziee_stu_name}," +
            "      hziee_stu_address=#{hziee_stu_address},   " +
            "      hziee_stu_telephone=#{hziee_stu_telephone},  " +
            "      hziee_stu_pro=#{hziee_stu_pro},   " +
            "      hziee_stu_age=#{hziee_stu_age},   " +
            "      hziee_stu_pwd=#{hziee_stu_pwd},   " +
            "      hziee_stu_status=#{hziee_stu_status}   " +
            "      where hziee_stu_no=#{hziee_stu_no}   ")
    int HzieeStuUpdate(HzieeStuInfo stu_info);
```

知识点：
- 参数是**一个对象**（`HzieeStuInfo`）时，`#{hziee_stu_name}` 直接取对象的属性，不需要 `@Param`。
- 参数是**简单类型**（`int stu_no`）时，用 `@Param("stu_no")` 给它起名，SQL 里才能写 `#{stu_no}`。
- 学号是主键，只出现在 `where` 里定位记录，不放在 `set` 里修改。
- 字符串拼接时每段前后要留空格，否则会拼成 `...hziee_stu_infowhere...` 导致 SQL 语法错误。

### 1.2 改 Service：`service/HzieeStuService.java`

把你写的 `getHzieeStuByNo`、`updateHzieeStuInfo` 两个方法（连同上面的注释）**整体替换**成：

```java
    public HzieeStuInfo getAEditHzieeStuInfo(int stu_no) {
        HzieeStuInfo stu_info = null;
        stu_info = stuMapper.getAEditHzieeStuInfo(stu_no);

        return stu_info;
    }

    public int HzieeStuUpdate(HzieeStuInfo stu_info) {
        int flag = 0;
        flag = stuMapper.HzieeStuUpdate(stu_info);

        return flag;
    }
```

### 1.3 改 Controller：`webcontroller/WebHzieeStuController.java`

把 `web_hziee_stu_edit` 和 `web_hziee_stu_do_edit` 两个方法**整体替换**成下面两个：

```java
    @RequestMapping("web_hziee_stu_edit")
    public String webHzieeStuEdit(@RequestParam int hziee_stu_no, Model model) {
        System.out.println("hziee_stu_no = " + hziee_stu_no);
        //根据学号获取学生信息
        HzieeStuInfo stu_info = null;
        stu_info = stuService.getAEditHzieeStuInfo(hziee_stu_no);
        System.out.println("stu_info = " + stu_info.toString());
        model.addAttribute("stu_info", stu_info);

        //专业列表要获取，否则修改没有办法获取下拉列表
        List<HzieeProInfo> hzieeProInfoList = null;
        hzieeProInfoList = hziee_pro_service.getHzieeProList();
        for(HzieeProInfo pro: hzieeProInfoList){
            System.out.println("pro = " + pro.toString());
        }
        model.addAttribute("hziee_pro_list", hzieeProInfoList);


        return "show_hziee_stu_edit";
    }

    @RequestMapping("web_hziee_stu_update")
    public String webHzieeStuUpdate(@ModelAttribute HzieeStuInfo stu_info,
                                    RedirectAttributes attributes) {
        System.out.println("stu_info = " + stu_info.toString());
        //这里完成修改的操作。
        int flag = 0;
        flag = stuService.HzieeStuUpdate(stu_info);

        if(flag > 0){
            return "redirect:web_get_all_hziee_stu_list";
        }
        else{
            attributes.addAttribute("hziee_stu_no", stu_info.getHziee_stu_no());
            return "redirect:web_hziee_stu_edit";
        }

    }
```

知识点：
- 编辑页和「添加学生 version2」一样有专业下拉框，所以同样要用 `hziee_pro_service.getHzieeProList()` 取专业列表，放进 Model 的 `hziee_pro_list`。（`hziee_pro_service` 字段，以及 `HzieeProInfo`、`List`、`RedirectAttributes` 的 import 在 v6/v7 就已经有了，不用再加。）
- `@ModelAttribute HzieeStuInfo stu_info`：表单里每个 `name` 和实体属性同名的输入框，会被 Spring 自动装进这个对象。
- `attributes.addAttribute("hziee_stu_no", 学号)` + `redirect:web_hziee_stu_edit`，Spring 会自动拼成 `web_hziee_stu_edit?hziee_stu_no=学号`，比自己拼字符串更不容易写错。
- 列表页 `all_hziee_stu_list.html` 里的「修改」按钮早已指向 `/web_hziee_stu_edit?hziee_stu_no=...`，不用改。

### 1.4 新增页面：`resources/templates/show_hziee_stu_edit.html`

在 `src/main/resources/templates/` 下新建 `show_hziee_stu_edit.html`，内容如下：

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>修改学生信息 - HZIEE</title>

    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: "Microsoft YaHei", Arial, sans-serif;
        }

        body {
            background: linear-gradient(135deg, #4b6cb7, #182848);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: flex-start;
            padding: 40px 0;
        }

        .container {
            width: 580px;
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
            padding: 35px 40px 30px;
        }

        .page-title {
            text-align: center;
            margin-bottom: 28px;
        }

        .page-title h2 {
            color: #333;
            font-weight: 600;
        }

        .page-title p {
            color: #999;
            font-size: 14px;
            margin-top: 6px;
        }

        .form-group {
            margin-bottom: 18px;
        }

        .form-group label {
            display: block;
            margin-bottom: 6px;
            color: #555;
            font-size: 14px;
            font-weight: 500;
        }

        .form-group input,
        .form-group select {
            width: 100%;
            height: 40px;
            padding: 0 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
            transition: border-color 0.2s;
            background-color: #fff;
        }

        .form-group input:focus,
        .form-group select:focus {
            border-color: #4b6cb7;
            outline: none;
        }

        /* 只读输入框样式 */
        .form-group input[readonly] {
            background-color: #f5f5f5;
            color: #888;
            cursor: not-allowed;
        }

        .btn-group {
            display: flex;
            justify-content: space-between;
            gap: 15px;
            margin-top: 30px;
        }

        .btn {
            flex: 1;
            height: 42px;
            border: none;
            border-radius: 4px;
            font-size: 15px;
            cursor: pointer;
            transition: opacity 0.2s;
        }

        .btn-submit {
            background: linear-gradient(to right, #4b6cb7, #182848);
            color: #fff;
        }

        .btn-submit:hover {
            opacity: 0.9;
        }

        .btn-back {
            background-color: #f0f0f0;
            color: #333;
        }

        .btn-back:hover {
            background-color: #e0e0e0;
        }

        .footer {
            text-align: center;
            margin-top: 22px;
            font-size: 12px;
            color: #aaa;
        }
    </style>
</head>

<body>

<div class="container">
    <div class="page-title">
        <h2>修改学生信息</h2>
        <p>HZIEE Edit Student</p>
    </div>

    <!-- 修改表单 -->
    <form action="/web_hziee_stu_update" method="get" th:object="${stu_info}">

        <!-- 学号：只读，不允许修改 -->
        <div class="form-group">
            <label for="hziee_stu_no">学号</label>
            <input type="number"
                   id="hziee_stu_no"
                   th:field="*{hziee_stu_no}"
                   readonly>
        </div>

        <div class="form-group">
            <label for="hziee_stu_name">姓名</label>
            <input type="text"
                   id="hziee_stu_name"
                   th:field="*{hziee_stu_name}"
                   placeholder="请输入姓名"
                   required>
        </div>

        <!-- 专业：下拉选择 + 自动回显 -->
        <div class="form-group">
            <label for="hziee_stu_pro">专业</label>
            <select id="hziee_stu_pro" th:field="*{hziee_stu_pro}" required>
                <option value="">-- 请选择专业 --</option>
                <option th:each="pro : ${hziee_pro_list}"
                        th:value="${pro.hziee_pro_name}"
                        th:text="${pro.hziee_pro_name}">
                </option>
            </select>
        </div>

        <div class="form-group">
            <label for="hziee_stu_age">年龄</label>
            <input type="number"
                   id="hziee_stu_age"
                   th:field="*{hziee_stu_age}"
                   placeholder="请输入年龄"
                   required min="1" max="150">
        </div>

        <div class="form-group">
            <label for="hziee_stu_telephone">电话</label>
            <input type="text"
                   id="hziee_stu_telephone"
                   th:field="*{hziee_stu_telephone}"
                   placeholder="请输入电话号码"
                   required>
        </div>

        <div class="form-group">
            <label for="hziee_stu_address">地址</label>
            <input type="text"
                   id="hziee_stu_address"
                   th:field="*{hziee_stu_address}"
                   placeholder="请输入地址"
                   required>
        </div>

        <div class="form-group">
            <label for="hziee_stu_pwd">密码</label>
            <input type="password"
                   id="hziee_stu_pwd"
                   th:field="*{hziee_stu_pwd}"
                   placeholder="请输入密码"
                   required>
        </div>

        <div class="form-group">
            <label for="hziee_stu_status">状态</label>
            <select id="hziee_stu_status" th:field="*{hziee_stu_status}">
                <option value="1">正常</option>
                <option value="0">异常</option>
            </select>
        </div>

        <div class="btn-group">
            <button type="button" class="btn btn-back" onclick="window.location.href='/hziee/stu_list'">返回列表</button>
            <button type="submit" class="btn btn-submit">保存修改</button>
        </div>
    </form>

    <div class="footer">
        © HZIEE Student System
    </div>
</div>

</body>
</html>
```

页面要点：
- `th:object="${stu_info}"` + `th:field="*{属性名}"`：一次完成**回显**（把 value 填成数据库里的值）和**命名**（生成同名的 `name`/`id`），提交时正好对应 `@ModelAttribute`。
- 学号输入框带 `readonly`：能提交、但不能改。注意不能用 `disabled`，disabled 的输入框不会被提交，后台就拿不到学号了。
- 专业下拉框用 `th:each` 遍历 `hziee_pro_list` 生成选项，再加上 `th:field="*{hziee_stu_pro}"`，Thymeleaf 会自动给当前专业那一项加 `selected`，这就是「下拉框回显」。
- 表单 `action="/web_hziee_stu_update"`，和 1.3 的请求名对应。

### 1.5 验证 v8

1. 启动项目，打开 http://localhost:8080/web_get_all_hziee_stu_list
2. 点某个学生的「修改」→ 进入编辑页，所有字段和专业下拉框都已回显。
3. 改个姓名 → 保存修改 → 回到列表，数据已更新。

```bash
git add -A
git commit -m "v8: 修改学生信息（编辑页 + 专业下拉框回显）"
```

---

## 第 2 步：升级到 v9 —— 按条件模糊查询 + 分页

目标：在页面上选择「按哪个字段查」、输入关键字、选择每页几条，查出结果并能翻页。

需要把「当前第几页、每页几条、查哪一列、查什么值」这几个参数打包在一起传给 Mapper，所以先新建一个实体类。

### 2.1 新增实体：`entity/HzieePageInfo.java`

在 `entity` 包下新建 `HzieePageInfo.java`：

```java
package com.software2026_12.entity;

public class HzieePageInfo {
    private int current_page;
    private int total_page;
    private int page_size;
    private String qry_column_name;
    private String qry_value;

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public int getTotal_page() {
        return total_page;
    }

    public void setTotal_page(int total_page) {
        this.total_page = total_page;
    }

    public int getPage_size() {
        return page_size;
    }

    public void setPage_size(int page_size) {
        this.page_size = page_size;
    }

    public String getQry_column_name() {
        return qry_column_name;
    }

    public void setQry_column_name(String qry_column_name) {
        this.qry_column_name = qry_column_name;
    }

    public String getQry_value() {
        return qry_value;
    }

    public void setQry_value(String qry_value) {
        this.qry_value = qry_value;
    }

    @Override
    public String toString() {
        return "HzieePageInfo{" +
                "current_page=" + current_page +
                ", total_page=" + total_page +
                ", page_size=" + page_size +
                ", qry_column_name='" + qry_column_name + '\'' +
                ", qry_value='" + qry_value + '\'' +
                '}';
    }
}

```

| 属性 | 含义 |
|---|---|
| `current_page` | 当前第几页 |
| `total_page` | 总页数（v9/v10 里还没有计算，见文末「遗留问题」） |
| `page_size` | 每页几条 |
| `qry_column_name` | 按哪一列查，例如 `hziee_stu_name` |
| `qry_value` | 查询关键字 |

### 2.2 改 Mapper：`mapper/HzieeStuMapper.java`

**① 在 import 区加一行**（放在 `import com.software2026_12.entity.HzieeStuInfo;` 上面）：

```java
import com.software2026_12.entity.HzieePageInfo;
```

**② 在文件末尾**、`HzieeStuUpdate` 方法之后、最后一个 `}` 之前加入：

```java
    @Select("select  * from hziee_stu_info   " +
            "    where ${qry_column_name} like concat('%',#{qry_value},'%') " +
            "    limit #{current_page - 1}, #{page_size}  ")
    List<HzieeStuInfo> qryHzieeStuList(HzieePageInfo pageInfo);
```

> 这里先**原样照抄 v9**（包括 `#{current_page - 1}`），第 3 步会解释它为什么跑不起来并修改。

知识点：`${}` 和 `#{}` 的区别（本节课重点）

| 写法 | 效果 | 用在哪 |
|---|---|---|
| `#{qry_value}` | 预编译占位符 `?`，值会被当成字符串加引号，能防 SQL 注入 | 普通的**值** |
| `${qry_column_name}` | 原样**拼接**到 SQL 字符串里 | **列名、表名**等不能加引号的地方 |

列名如果用 `#{}`，SQL 会变成 `where 'hziee_stu_name' like ...`（拿一个字符串常量去比较），查不出想要的结果；所以列名只能用 `${}`。但 `${}` 有 SQL 注入风险，实际项目中要在后台检查列名是否在白名单里。

`like concat('%', #{qry_value}, '%')`：模糊查询，相当于「包含关键字」。

`limit 偏移量, 条数`：MySQL 分页语法，偏移量从 0 开始，表示跳过多少条。

### 2.3 改 Service：`service/HzieeStuService.java`

**① import 区加一行**（放在 `import com.software2026_12.entity.HzieeStuInfo;` 上面）：

```java
import com.software2026_12.entity.HzieePageInfo;
```

**② 在类末尾**（`HzieeStuUpdate` 之后）加入：

```java
    public List<HzieeStuInfo> qryHzieeStuList(HzieePageInfo hziee_page_info) {
        List<HzieeStuInfo> stu_list = null;
        stu_list = stuMapper.qryHzieeStuList(hziee_page_info);

        return stu_list;
    }
```

### 2.4 改 Controller：`webcontroller/WebHzieeStuController.java`

**① import 区加一行**（放在 `import com.software2026_12.entity.HzieeProInfo;` 上面）：

```java
import com.software2026_12.entity.HzieePageInfo;
```

**② 在类末尾**（`webHzieeStuUpdate` 方法之后）加入新方法：

```java
    @RequestMapping("web_qry_hziee_stu_list")
    public String webQryHzieeStuList(@RequestParam int current_page,
                                     @RequestParam int page_size,
                                     @RequestParam String qry_column_name,
                                     @RequestParam String qry_value,
                                     Model model) {
        HzieePageInfo hziee_page_info = new HzieePageInfo();
        hziee_page_info.setCurrent_page(current_page);
        hziee_page_info.setPage_size(page_size);
        hziee_page_info.setQry_column_name(qry_column_name);
        hziee_page_info.setQry_value(qry_value);
        System.out.println("hziee_page_info = " + hziee_page_info.toString());
        model.addAttribute("hziee_page_info", hziee_page_info);

        //根据查询条件去查询list数据
        List<HzieeStuInfo> hziee_stu_list = null;
        hziee_stu_list = stuService.qryHzieeStuList(hziee_page_info);
        System.out.println("获取数据条数:" + hziee_stu_list.size());
        for (HzieeStuInfo stu : hziee_stu_list) {
            System.out.println("stu = " + stu.toString());
        }

        model.addAttribute("hziee_stu_list", hziee_stu_list);

        return "qry_hziee_stu_list";
    }
```

> 小细节：v9 里 `webHzieeStuUpdate` 末尾 `}` 前的那个空行被挪到了新方法 `return` 的前面，只是空行位置不同，不影响运行。想和 v10 逐字一致的话，把 `webHzieeStuUpdate` 末尾 `}` 前的空行删掉即可。

知识点：
- 4 个 `@RequestParam` 接收 URL 参数，打包成 `HzieePageInfo` 交给 Service。
- `hziee_page_info` 放进 Model 有两个用途：让查询表单**回显**上次的查询条件；让分页按钮知道当前页，拼出上一页/下一页的链接。

### 2.5 新增页面：`resources/templates/qry_hziee_stu_list.html`

在 `templates/` 下新建 `qry_hziee_stu_list.html`：

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>学生信息查询 - HZIEE</title>

    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: "Microsoft YaHei", Arial, sans-serif;
        }

        body {
            background: linear-gradient(135deg, #4b6cb7, #182848);
            min-height: 100vh;
            padding: 30px 0;
        }

        .container {
            width: 1150px;
            margin: 0 auto;
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
            padding: 30px 35px;
        }

        .page-title {
            text-align: center;
            margin-bottom: 25px;
        }

        .page-title h2 {
            color: #333;
            font-weight: 600;
        }

        .page-title p {
            color: #999;
            font-size: 14px;
            margin-top: 6px;
        }

        /* 工具栏 */
        .toolbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        .btn {
            height: 38px;
            padding: 0 18px;
            border: none;
            border-radius: 4px;
            font-size: 14px;
            cursor: pointer;
            transition: opacity 0.2s;
        }

        .btn-add {
            background: linear-gradient(to right, #4b6cb7, #182848);
            color: #fff;
        }

        .btn-add:hover {
            opacity: 0.9;
        }

        /* 查询表单 */
        .search-form {
            display: flex;
            gap: 10px;
            align-items: center;
            flex-wrap: wrap;
        }

        .search-form select,
        .search-form input {
            height: 38px;
            padding: 0 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
        }

        .search-form select:focus,
        .search-form input:focus {
            border-color: #4b6cb7;
            outline: none;
        }

        .btn-search {
            background-color: #4b6cb7;
            color: #fff;
        }

        .btn-search:hover {
            opacity: 0.9;
        }

        /* 表格 */
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }

        thead th {
            background-color: #f5f7fa;
            color: #333;
            font-weight: 600;
            padding: 12px 8px;
            text-align: center;
            border-bottom: 2px solid #e0e0e0;
        }

        tbody td {
            padding: 11px 8px;
            text-align: center;
            border-bottom: 1px solid #f0f0f0;
            color: #333;
            font-size: 14px;
        }

        tbody tr:hover {
            background-color: #f9fafc;
        }

        /* 操作按钮 */
        .btn-edit {
            background-color: #409eff;
            color: #fff;
            margin-right: 6px;
            height: 32px;
            padding: 0 12px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 13px;
        }

        .btn-edit:hover {
            opacity: 0.85;
        }

        .btn-delete {
            background-color: #e74c3c;
            color: #fff;
            height: 32px;
            padding: 0 12px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 13px;
        }

        .btn-delete:hover {
            opacity: 0.85;
        }

        /* 分页栏 */
        .pagination {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 25px;
            padding-top: 20px;
            border-top: 1px solid #eee;
        }

        .page-info {
            color: #666;
            font-size: 14px;
        }

        .page-info span {
            color: #4b6cb7;
            font-weight: 600;
        }

        .page-buttons {
            display: flex;
            gap: 8px;
        }

        .page-btn {
            height: 34px;
            padding: 0 14px;
            border: 1px solid #ddd;
            border-radius: 4px;
            background-color: #fff;
            color: #333;
            cursor: pointer;
            font-size: 13px;
            transition: all 0.2s;
        }

        .page-btn:hover:not(:disabled) {
            border-color: #4b6cb7;
            color: #4b6cb7;
        }

        .page-btn:disabled {
            background-color: #f5f5f5;
            color: #bbb;
            cursor: not-allowed;
        }

        .page-btn.active {
            background-color: #4b6cb7;
            color: #fff;
            border-color: #4b6cb7;
        }

        .footer {
            text-align: center;
            margin-top: 25px;
            font-size: 12px;
            color: #aaa;
        }
    </style>
</head>

<body>

<div class="container">
    <div class="page-title">
        <h2>学生信息查询</h2>
        <p>HZIEE Student Query &amp; List</p>
    </div>

    <!-- 工具栏 -->
    <div class="toolbar">
        <button class="btn btn-add" onclick="window.location.href='/hziee/stu_add'">+ 添加学生</button>

        <!-- 查询表单 -->
        <form class="search-form" action="/web_qry_hziee_stu_list" method="get" th:object="${hziee_page_info}">
            <!-- 查询字段 -->
            <select name="qry_column_name" th:field="*{qry_column_name}">
                <option value="hziee_stu_no">学号</option>
                <option value="hziee_stu_name">姓名</option>
                <option value="hziee_stu_pro">专业</option>
                <option value="hziee_stu_telephone">电话</option>
                <option value="hziee_stu_address">地址</option>
            </select>

            <!-- 查询关键字 -->
            <input type="text" name="qry_value" th:field="*{qry_value}" placeholder="请输入查询内容">

            <!-- 每页条数 -->
            <select name="page_size" th:field="*{page_size}" onchange="this.form.submit()">
                <option value="5">5条/页</option>
                <option value="10">10条/页</option>
                <option value="20">20条/页</option>
                <option value="50">50条/页</option>
            </select>

            <!-- 当前页码（隐藏域，查询时重置为第1页） -->
            <input type="hidden" name="current_page" value="1">

            <button type="submit" class="btn btn-search">查询</button>
        </form>
    </div>

    <!-- 学生表格 -->
    <table>
        <thead>
        <tr>
            <th>学号</th>
            <th>姓名</th>
            <th>专业</th>
            <th>年龄</th>
            <th>电话</th>
            <th>地址</th>
            <th>状态</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr th:each="stu : ${hziee_stu_list}">
            <td th:text="${stu.hziee_stu_no}"></td>
            <td th:text="${stu.hziee_stu_name}"></td>
            <td th:text="${stu.hziee_stu_pro}"></td>
            <td th:text="${stu.hziee_stu_age}"></td>
            <td th:text="${stu.hziee_stu_telephone}"></td>
            <td th:text="${stu.hziee_stu_address}"></td>
            <td>
                <span th:if="${stu.hziee_stu_status == 1}">正常</span>
                <span th:if="${stu.hziee_stu_status != 1}">异常</span>
            </td>
            <td>
                <button class="btn-edit"
                        th:onclick="|window.location.href='/hziee/stu_edit?no=${stu.hziee_stu_no}'|">
                    修改
                </button>
                <button class="btn-delete"
                        th:onclick="|if(confirm('确认删除该学生吗？')) window.location.href='/hziee/stu_delete?no=${stu.hziee_stu_no}'|">
                    删除
                </button>
            </td>
        </tr>

        <!-- 空数据提示 -->
        <tr th:if="${#lists.isEmpty(hziee_stu_list)}">
            <td colspan="8" style="color:#999; padding: 20px;">暂无学生数据</td>
        </tr>
        </tbody>
    </table>

    <!-- 分页栏 -->
    <div class="pagination" th:if="${hziee_page_info.total_page > 0}">
        <div class="page-info">
            第 <span th:text="${hziee_page_info.current_page}"></span> 页 /
            共 <span th:text="${hziee_page_info.total_page}"></span> 页
            （每页 <span th:text="${hziee_page_info.page_size}"></span> 条）
        </div>

        <div class="page-buttons">
            <!-- 首页 -->
            <button class="page-btn"
                    th:disabled="${hziee_page_info.current_page <= 1}"
                    th:onclick="|window.location.href='/web_qry_hziee_stu_list?current_page=1&page_size=${hziee_page_info.page_size}&qry_column_name=${hziee_page_info.qry_column_name}&qry_value=${hziee_page_info.qry_value}'|">
                首页
            </button>

            <!-- 上一页 -->
            <button class="page-btn"
                    th:disabled="${hziee_page_info.current_page <= 1}"
                    th:onclick="|window.location.href='/web_qry_hziee_stu_list?current_page=${hziee_page_info.current_page - 1}&page_size=${hziee_page_info.page_size}&qry_column_name=${hziee_page_info.qry_column_name}&qry_value=${hziee_page_info.qry_value}'|">
                上一页
            </button>

            <!-- 下一页 -->
            <button class="page-btn"
                    th:disabled="${hziee_page_info.current_page >= hziee_page_info.total_page}"
                    th:onclick="|window.location.href='/web_qry_hziee_stu_list?current_page=${hziee_page_info.current_page + 1}&page_size=${hziee_page_info.page_size}&qry_column_name=${hziee_page_info.qry_column_name}&qry_value=${hziee_page_info.qry_value}'|">
                下一页
            </button>

            <!-- 尾页 -->
            <button class="page-btn"
                    th:disabled="${hziee_page_info.current_page >= hziee_page_info.total_page}"
                    th:onclick="|window.location.href='/web_qry_hziee_stu_list?current_page=${hziee_page_info.total_page}&page_size=${hziee_page_info.page_size}&qry_column_name=${hziee_page_info.qry_column_name}&qry_value=${hziee_page_info.qry_value}'|">
                尾页
            </button>
        </div>
    </div>

    <div class="footer">
        © HZIEE Student System
    </div>
</div>

</body>
</html>
```

页面要点：
- 查询表单 `th:object="${hziee_page_info}"`，三个控件用 `th:field` 回显上次的条件。
- 每页条数下拉框 `onchange="this.form.submit()"`：一改就自动提交。
- 隐藏域 `current_page=1`：重新查询时总是从第 1 页开始。
- 分页按钮用 `|...${}...|`（Thymeleaf 字面量替换）拼出带全部条件的 URL，翻页时查询条件不会丢。
- 「首页 / 上一页」在第 1 页时 `th:disabled`，「下一页 / 尾页」在最后一页时 disabled。

### 2.6 验证 v9

访问：

```
http://localhost:8080/web_qry_hziee_stu_list?current_page=1&page_size=5&qry_column_name=hziee_stu_name&qry_value=
```

你会发现**报错**，控制台里大致是：

```
There is no getter for property named 'current_page - 1' in 'class com.software2026_12.entity.HzieePageInfo'
```

这就是 v10 要修的问题。v9 先照样提交：

```bash
git add -A
git commit -m "v9: 条件模糊查询 + 分页（HzieePageInfo、qry_hziee_stu_list 页面）"
```

---

## 第 3 步：升级到 v10 —— 修正 `limit`

### 3.1 为什么 `#{current_page - 1}` 会报错

`#{}` 里只能写**属性名**，MyBatis 不会在里面做加减运算。它会把 `current_page - 1` 整个当成一个属性名，去 `HzieePageInfo` 里找对应的 getter，自然找不到。

### 3.2 修改 `mapper/HzieeStuMapper.java`

把 `qryHzieeStuList` 上面 SQL 的最后一行：

```java
            "    limit #{current_page - 1}, #{page_size}  ")
```

改成：

```java
            "    limit #{current_page}, #{page_size}  ")
```

完整方法：

```java
    @Select("select  * from hziee_stu_info   " +
            "    where ${qry_column_name} like concat('%',#{qry_value},'%') " +
            "    limit #{current_page}, #{page_size}  ")
    List<HzieeStuInfo> qryHzieeStuList(HzieePageInfo pageInfo);
```

### 3.3 验证 v10

再访问 2.6 的地址，页面能正常显示查询结果了。

```bash
git add -A
git commit -m "v10: 修正分页 SQL 的 limit 写法"
```

### 3.4 对照检查：确认已和第 10 版一致

在项目根目录执行（Git Bash）：

```bash
diff -r --strip-trailing-cr src "_old_versions/software2026_12(10)/src"
```

正常情况下只剩 `application.properties` 的两行差异（你自己的数据库密码和新驱动），说明已经完全长成第 10 版。

---

## 遗留问题与课后思考（v10 原样保留，教程里没有修改）

1. **分页偏移量不对**：`limit` 的第一个参数是「跳过多少条」，应该是 `(current_page - 1) * page_size`。v10 直接用了页码，第 1 页会跳过 1 条记录，第 2 页只跳过 2 条。正确做法是先在 Controller/Service 里算好偏移量，比如给 `HzieePageInfo` 加一个 `offset` 属性：`offset = (current_page - 1) * page_size`，SQL 写 `limit #{offset}, #{page_size}`。
2. **总页数没算**：`total_page` 一直是 0，页面上的分页栏 `th:if="${hziee_page_info.total_page > 0}"` 永远不显示。需要再写一条 `select count(*) from hziee_stu_info where ${qry_column_name} like ...` 查出总条数，然后 `total_page = (总条数 + page_size - 1) / page_size`。
3. **`${}` 的注入风险**：`qry_column_name` 直接拼进 SQL，应在后台校验它只能是那 5 个列名之一。
4. **几处链接指向不存在的地址**：编辑页「返回列表」是 `/hziee/stu_list`；查询页的添加 / 修改 / 删除按钮是 `/hziee/stu_add`、`/hziee/stu_edit?no=`、`/hziee/stu_delete?no=`，后端都没有这些请求。可以分别改成 `/web_get_all_hziee_stu_list`、`/web_show_hziee_stu_add`、`/web_hziee_stu_edit?hziee_stu_no=`、`/web_hziee_stu_delete?hziee_stu_no=`。
5. **编辑页空指针**：`webHzieeStuEdit` 在判空之前就调用了 `stu_info.toString()`，学号不存在时会抛 `NullPointerException`。你原来写的「查不到就重定向回列表」的判空逻辑其实更稳妥。
6. 列表页 `all_hziee_stu_list.html` 的搜索表单仍指向 `/hziee/stu_search`，可以改成 `/web_qry_hziee_stu_list`，并补上 `current_page`、`page_size` 两个隐藏域，把两个页面串起来。
