# Project 4A 实操教程：从当前骨架一步步做到真实词频曲线

> 写法参考 `TUTORIAL_v8-v10.md`：认清起点 → 明确改哪里 → 放入代码 → 解释这一小步 → 运行验证。
>
> 本文依据当前 `proj4a/src`、`tests`、实际数据和 SP26 项目说明编写。只更新教程，不替你修改源码。
>
> 自学提醒：课程明确禁止提交 LLM 生成的代码。本文参考代码用于自学体验；正式修课请遵守课程规定。

## 怎么读这份教程

**先顺序读到第 9 步，后面的 B/C/D 暂时不要看。**

你不需要先学会服务器、HTTP、JSON、图片编码。这些由现成代码处理。我们先把它们当作“已经装好的显示设备”：把正确数据交进去，就能看到文字和图。

每一步都有验证。当前步骤没通过，不往下堆功能。不要直接把整份文档的代码一口气复制完，否则很难知道自己究竟理解到哪里。

本文所有方法代码都包含方法声明：**替换整个同名方法，不是在原方法内部再粘一个方法。**

---

## 总览：这次究竟要改什么？

| 阶段 | 你会看到的变化 | 修改文件 |
|---|---|---|
| 第 0 步 | 知道哪些已经提供，哪些需要自己做 | 不改代码 |
| 第 1 步 | 默认演示图能显示，不再报 Y-Axis null | `TimeSeries.java` |
| 第 2 步 | 能复制指定年份的数据 | `TimeSeries.java` |
| 第 3 步 | 能把两条年度数据相加 | `TimeSeries.java` |
| 第 4 步 | 能算每年的比例 | `TimeSeries.java` |
| 第 5 步 | 把文件读进内存 | `NGramMap.java` |
| 第 6 步 | 给单词和年份，能查到次数 | `NGramMap.java` |
| 第 7 步 | 能查相对频率，能合并多个词的频率 | `NGramMap.java` |
| 第 8 步 | 网页显示真实词频文字 | 新增 `HistoryTextHandler.java`，修改 `Main.java` |
| 第 9 步 | 网页显示真实词频曲线 | 新增 `HistoryHandler.java`，修改 `Main.java` |
| 收尾 | 巩固 A，再独立进入 B/C/D | 测试、思考和路线 |

另建一个 `tests/TutorialTest.java`，每阶段加一个小测试。原来的测试保留。

项目最核心的关系只有这一条：

```text
文件中的记录 → 按单词保存 → 按年份查询 → 算比例 → 显示文字或图片
```

---

## 第 0 步：认清当前起点

### 0.1 当前代码不是空项目

打开 `src/main/Main.java`，当前 main 已经在做这些事：

```java
NgordnetServer hns = new NgordnetServer();
hns.startUp();
hns.register("history", new DummyHistoryHandler());
hns.register("historytext", new DummyHistoryTextHandler());
```

现在先把它翻译成普通话：

1. 创建现成的服务器。
2. 设置服务器。
3. 点 History 时，交给 `DummyHistoryHandler`。
4. 点 History (Text) 时，交给 `DummyHistoryTextHandler`。

`Dummy` 就是演示用的，不是真正查询单词的实现。

### 0.2 哪些东西不用你写？

| 文件或目录 | 作用 | 现在是否要研究实现 |
|---|---|---|
| `static/` | 浏览器里的输入框、按钮、显示区域 | 不用 |
| `browser/NgordnetServer.java` | 启动服务、注册处理器 | 不用 |
| `browser/NgordnetQueryHandler.java` | 将浏览器参数转换成查询对象 | 先不用 |
| `browser/Plotter.java` | 根据数据生成图 | 只需会调用 |
| `demo/` | 可模仿的示例 | 用到时再看 |
| `main/TimeSeries.java` | 一条按年份排列的数据 | 需要你完成 |
| `main/NGramMap.java` | 按单词查询年度数据 | 需要你完成 |

不是每个出现在项目里的类都要你重新学一遍。我们先完成最后两项。

### 0.3 先确认运行位置

在 IntelliJ 里运行 `main.Main`，工作目录设为当前 `proj4a`，不是整个 `skeleton-sp26`。

原因：

```text
./data/   相对于运行工作目录
static/   也相对于运行工作目录
```

数据已经在本地，约 133.26 MiB，不必重复下载。

打开：

```text
http://localhost:4567/ngordnet_4a.html
```

预期：

- History (Text) 显示演示提示。
- History 绘图报 500，控制台有 `Y-Axis data cannot be null`。

这是我们已经复现过的问题，第 1 步就修它。

**不要修改 `Main` 上方标注不可改动的常量和初始化代码。**

---

## 第 1 步：先让默认演示图显示出来

### 1.1 为什么明明是画图，先改 TimeSeries？

`DummyHistoryHandler` 已经创建了两条数据：抛物线和正弦波。绘图时调用：

```java
chart.addSeries(word, ts.years(), ts.data());
```

但你的 `years()`、`data()` 都返回 null。图表拿不到横轴和纵轴数据，自然报错。

先不用理解图片怎么生成。就像给 Excel 画图，必须先给它两列数据。

### 1.2 认识 TimeSeries 已经具备的能力

文件：`src/main/TimeSeries.java`。

它的声明是：

```java
public class TimeSeries extends TreeMap<Integer, Double>
```

意思是：TimeSeries 就是一种 TreeMap，key 是年份 Integer，value 是数值 Double。

它已经继承了这些方法，不需要自己再写：

```java
ts.put(2001, 20.0);   // 保存一项
ts.get(2001);         // 取出这一年的数值
ts.keySet();          // 所有年份
ts.values();          // 对应的所有数值
```

TreeMap 的特点是按 key 排序。即使先放 2001，再放 2000，遍历时仍然先得到 2000。

**因此不要在 TimeSeries 里面再声明一个 TreeMap 字段。它本身已经是了。**

### 1.3 先加一个小测试

在 `tests/` 下新建 `TutorialTest.java`，内容如下：

```java
import main.TimeSeries;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TutorialTest {
    @Test
    public void testYearsAndData() {
        TimeSeries ts = new TimeSeries();
        ts.put(2001, 20.0);
        ts.put(2000, 10.0);

        assertThat(ts.years()).containsExactly(2000, 2001).inOrder();
        assertThat(ts.data()).containsExactly(10.0, 20.0).inOrder();
    }
}
```

在 IntelliJ 中只运行 `testYearsAndData`。修改前会失败，因为两个方法返回 null。

`containsExactly(...).inOrder()` 的意思就是“内容必须恰好是这些，并且顺序一致”。先会读测试的预期，不必现在研究整个测试框架。

### 1.4 改 `src/main/TimeSeries.java`

**① import 区增加：**

```java
import java.util.ArrayList;
```

保留原有的 `List` 和 `TreeMap` 导入。

**② 把整个 `years()` 方法替换成：**

```java
public List<Integer> years() {
    return new ArrayList<>(keySet());
}
```

**③ 把整个 `data()` 方法替换成：**

```java
public List<Double> data() {
    return new ArrayList<>(values());
}
```

知识点：

- `keySet()` 给出年份，`values()` 给出相同年份顺序对应的数值。
- 这两者不是方法声明所需的 List，所以用 ArrayList 接收并复制元素。
- 返回的是新列表，调用者改这个列表不会改原来的 TimeSeries。

例子：

```text
原数据：{2000=10.0, 2001=20.0}
years：[2000, 2001]
data ：[10.0, 20.0]
```

### 1.5 验证

1. 重跑 `testYearsAndData`：应通过。
2. 停止旧的 Main，再重新运行。
3. 打开网页，点击 History。
4. 应出现抛物线和正弦波。

**此时还不是 cat/dog 曲线，这是正确的阶段结果。** 我们只修好了演示数据的输出方法，尚未接入真实文件。

不要要求整个测试套件现在都通过，其他方法还没做。

**想一想：** 为什么我们没改 Plotter，图片就能出现？你能说出数据从哪个方法拿出来的吗？

---

## 第 2 步：复制某一段年份的数据

### 2.1 这一步解决什么问题？

假设已有：

```text
{1999=1.0, 2000=2.0, 2002=3.0, 2003=4.0}
```

用户只想看 2000–2002，应得到：

```text
{2000=2.0, 2002=3.0}
```

注意：

- 包含起止年份。
- 2001 原本没有数据，不补 0。
- 返回独立的新对象，修改它不能破坏原数据。

### 2.2 先加测试

在 `tests/TutorialTest.java` 最后一个 `}` 之前加入：

```java
@Test
public void testRangeCopy() {
    TimeSeries original = new TimeSeries();
    original.put(1999, 1.0);
    original.put(2000, 2.0);
    original.put(2002, 3.0);
    original.put(2003, 4.0);

    TimeSeries copy = new TimeSeries(original, 2000, 2002);
    assertThat(copy.years()).containsExactly(2000, 2002).inOrder();
    copy.put(2000, 99.0);
    assertThat(original.get(2000)).isEqualTo(2.0);
}
```

先运行，确认当前的空构造逻辑不满足预期。

### 2.3 改 `src/main/TimeSeries.java`

找到带三个参数的构造器，整体替换为：

```java
public TimeSeries(TimeSeries ts, int startYear, int endYear) {
    super();
    for (int year : ts.keySet()) {
        if (year >= startYear && year <= endYear) {
            put(year, ts.get(year));
        }
    }
}
```

逐行解释：

- `super()`：先创建继承来的空 TreeMap。
- `ts`：要被复制的原序列。
- `ts.keySet()`：遍历原序列所有年份。
- `if`：只选范围内的年份。
- `put(...)`：写进正在创建的这个新对象，不是写回 ts。

无参构造器 `public TimeSeries()` 原样保留，不需要改。

### 2.4 这一小步才引入“防御性复制”

名字听起来复杂，其实是：

> 给你一份可修改的副本，不让你直接改我的内部数据。

这里 value 是不可变的 Double，复制映射项就足够。以后如果 value 是可变 List，则还要考虑是否复制 List 本身；现在不用扩大问题。

### 2.5 验证

运行 `testRangeCopy`，再运行 TutorialTest 中已有的两个测试，都应通过。

本文实现遇到倒置范围时自然返回空序列，没有额外设计异常。

**想一想：** 如果直接写 `TimeSeries copy = original;`，再修改 copy，原对象会不会变？这和 `new TimeSeries(...)` 差在哪？

---

## 第 3 步：把两条年度数据相加

### 3.1 先手算

```text
A = {2000=2.0, 2002=6.0}
B = {2001=3.0, 2002=4.0}

结果 = {2000=2.0, 2001=3.0, 2002=10.0}
```

规则：

- 任意一边存在的年份都保留。
- 同一年两边都有，就相加。
- 不改变 A，也不改变 B。

### 3.2 先加测试

仍放在 TutorialTest 类的最后一个 `}` 之前：

```java
@Test
public void testPlus() {
    TimeSeries a = new TimeSeries();
    a.put(2000, 2.0);
    a.put(2002, 6.0);
    TimeSeries b = new TimeSeries();
    b.put(2001, 3.0);
    b.put(2002, 4.0);

    TimeSeries sum = a.plus(b);
    assertThat(sum.years()).containsExactly(2000, 2001, 2002).inOrder();
    assertThat(sum.data()).containsExactly(2.0, 3.0, 10.0).inOrder();
    assertThat(a.get(2002)).isEqualTo(6.0);
    assertThat(b.get(2002)).isEqualTo(4.0);
}
```

先运行，当前 `plus` 返回 null，测试会失败。

### 3.3 改 `src/main/TimeSeries.java`

把 `plus` 整体替换为：

```java
public TimeSeries plus(TimeSeries ts) {
    TimeSeries result = new TimeSeries();
    result.putAll(this);

    for (int year : ts.keySet()) {
        double sum = result.getOrDefault(year, 0.0) + ts.get(year);
        result.put(year, sum);
    }
    return result;
}
```

按执行过程看：

```text
先复制 A：result = {2000=2.0, 2002=6.0}
遇到 B 的 2001：result 里没有，0 + 3 = 3
遇到 B 的 2002：result 里有 6，6 + 4 = 10
```

知识点：

- 调用 `a.plus(b)` 时，`this` 是 a，`ts` 是 b。
- `putAll(this)` 将 a 中所有映射复制进 result。
- `getOrDefault(year, 0.0)`：有这个年份就取值，没有就临时用 0 参与这次计算。
- 全程只往 result 写，因此 a、b 不变。

这里的临时 0 不等于“为所有缺失年份补零”。我们只处理本来至少在一边存在的年份，不会制造 2003 等年份。

### 3.4 验证

运行 `testPlus`，再运行原有 `tests/TimeSeriesTest.java`。

原有测试主要依赖 years、data、plus，做到这里应该能通过；但这不代表 `dividedBy` 已完成。

**想一想：** 只遍历 A、只累加 A 已有的年份，会漏掉上例的哪一年？

---

## 第 4 步：同一年相除，为算比例做准备

### 4.1 为什么要这个方法？

之后要算：

```text
单词某年的次数 ÷ 那年所有单词的总次数
```

先不要把“单词”引进这个类。TimeSeries 只需要完成“同一年相除”。

例如：

```text
A = {2000=2.0, 2002=6.0}
B = {1999=99.0, 2000=4.0, 2002=3.0}

A / B = {2000=0.5, 2002=2.0}
```

要求：

- 只遍历左边 A 的年份。
- B 多出的 1999 忽略。
- B 缺少 A 的某个年份，抛 IllegalArgumentException。
- 返回新序列，不改变输入。
- 按课程保证，不必处理分母为 0。

### 4.2 先加测试

在 TutorialTest 类中加入：

```java
@Test
public void testDivision() {
    TimeSeries a = new TimeSeries();
    a.put(2000, 2.0);
    TimeSeries b = new TimeSeries();
    b.put(1999, 99.0);
    b.put(2000, 4.0);

    TimeSeries result = a.dividedBy(b);
    assertThat(result.years()).containsExactly(2000);
    assertThat(result.get(2000)).isEqualTo(0.5);
    assertThat(a.get(2000)).isEqualTo(2.0);
    assertThat(b.get(2000)).isEqualTo(4.0);
    assertThrows(IllegalArgumentException.class, () -> b.dividedBy(a));
    assertThat(new TimeSeries().dividedBy(b)).isEmpty();
}
```

最后两行的意思：

- 反过来除时，a 缺少 b 的 1999，应抛异常。
- 左边为空，没有年份需要计算，所以结果为空。

`assertThrows` 里的 `() -> ...` 表示把这次操作交给测试框架执行，检查它是否抛指定异常。先会用这一种写法即可。

### 4.3 改 `src/main/TimeSeries.java`

把 `dividedBy` 整体替换为：

```java
public TimeSeries dividedBy(TimeSeries ts) {
    TimeSeries result = new TimeSeries();
    for (int year : keySet()) {
        if (!ts.containsKey(year)) {
            throw new IllegalArgumentException("Missing year: " + year);
        }
        result.put(year, get(year) / ts.get(year));
    }
    return result;
}
```

知识点：

- 没写 `ts.` 的 `keySet()`、`get()`，作用于当前对象 this。
- `ts.containsKey(year)` 检查右边是否有这个年份。
- 不应该给缺少的分母补 0，更不应该悄悄忽略这个年份。

### 4.4 验证与小结

运行 TutorialTest 的全部测试，应有 4 个通过；再运行原 TimeSeriesTest。

此时 TimeSeries 全部完成。可以清理已失效的 TODO 注释，保留说明性的注释。

你刚才真正使用的只有：

```text
Map 的读取、写入、遍历
+ 条件判断
+ 创建新对象
+ 继承已有方法
```

并没有要求你先掌握整个 Web 技术栈。

**闭卷练习：** 不看代码，重新说一遍 plus 和 dividedBy 对“年份缺失”的处理有什么不同。

---

## 第 5 步：把文件里的数据读进 NGramMap

### 5.1 先看两种数据格式

打开 `data/word_history_size3.csv`。实际记录类似：

```text
airport    2007    175702    32788
airport    2008    173294    31271
request    2005    646179    81592
```

这几列之间是制表符 tab，不是逗号。

| 下标 | 内容 | 是否使用 |
|---|---|---|
| 0 | 单词 | 使用 |
| 1 | 年份 | 使用 |
| 2 | 出现次数 | 使用 |
| 3 | 出现该词的卷数 | 这里不使用 |

再看 `data/year_history.csv`：

```text
1470,984,10,1
1472,117652,902,2
```

这里确实是逗号分隔，使用第 0 列年份和第 1 列全年总词数。

**文件扩展名叫 csv，不代表里面一定用逗号。要看实际内容。**

### 5.2 先运行现成的读取示例

运行 `src/demo/FileReadDemo.java` 的 main，工作目录仍为 proj4a。

你应看到每行内容，以及分割出的第一个单词。

这个示例已经告诉我们如何使用 `In`。课程本身没有系统教 Java 文件读取，这里就是让你参考示例，不是要求你凭空写出来。

接下来只需会这几个调用：

```java
In input = new In(filename);  // 打开
input.hasNextLine();         // 是否还有下一行
input.readLine();            // 读下一行
input.close();               // 关闭
```

示例用 `isEmpty()` 判断；本文使用含义更直接的 `hasNextLine()` 来逐行读取，都是同一个 In 类的可用方法。

### 5.3 数据应该存成什么样？

先想我们要查什么：给一个单词，找到它全部年份的次数。

所以存成：

```text
"airport" → {2007=175702.0, 2008=173294.0}
"request" → {2005=646179.0, ...}
```

外层按单词查，里面就是刚完成的 TimeSeries。

另外单独保存所有单词的年度总量：

```text
{1470=984.0, 1472=117652.0, ...}
```

### 5.4 改 `src/main/NGramMap.java`：加 import 和字段

**① 在 import 区加入：**

```java
import edu.princeton.cs.algs4.In;
import java.util.HashMap;
import java.util.Map;
```

保留已有 Collection 和两个年份常量的静态导入。

**② 将 `// TODO: Add any necessary static/instance variables.` 替换为：**

```java
private final Map<String, TimeSeries> wordHistories;
private final TimeSeries totalCounts;
```

解释：

- `Map<String, TimeSeries>`：单词对应一条时间序列。
- 实际用 HashMap：按单词查即可，不需要按单词排序。
- totalCounts：年度总词数也是一条时间序列。
- `private`：不让外部直接操作字段。
- `final`：字段初始化后不换成另一个 Map，但 Map 内部仍可写数据。

这里的 NGramMap 不继承 HashMap，因为它不只是一个简单 Map，还需要组合两份数据完成查询。

### 5.5 替换构造器

```java
public NGramMap(String wordHistoryFilename, String yearHistoryFilename) {
    wordHistories = new HashMap<>();
    totalCounts = new TimeSeries();

    In wordsInput = new In(wordHistoryFilename);
    while (wordsInput.hasNextLine()) {
        String line = wordsInput.readLine();
        if (line.isBlank()) {
            continue;
        }
        String[] fields = line.split("\t");
        String word = fields[0];
        int year = Integer.parseInt(fields[1]);
        double count = Double.parseDouble(fields[2]);

        TimeSeries history = wordHistories.get(word);
        if (history == null) {
            history = new TimeSeries();
            wordHistories.put(word, history);
        }
        history.put(year, count);
    }
    wordsInput.close();

    In yearsInput = new In(yearHistoryFilename);
    while (yearsInput.hasNextLine()) {
        String line = yearsInput.readLine();
        if (line.isBlank()) {
            continue;
        }
        String[] fields = line.split(",");
        int year = Integer.parseInt(fields[0]);
        double count = Double.parseDouble(fields[1]);
        totalCounts.put(year, count);
    }
    yearsInput.close();
}
```

逐段理解，不用一次背完：

1. 先创建两个空容器。
2. 每次读单词文件的一行。
3. 按 tab 拆开，获得 word、year、count。
4. 查这个词是否已经有 TimeSeries。
5. 没有就新建并放到外层 Map；有就继续用原来的。
6. 将这一年的 count 放进这条 TimeSeries。
7. 读完单词文件，再读年度总量文件。

两个常见错误：

- **每遇到一行就给同一个词 new 一个 TimeSeries**：会覆盖前面的年份。
- **总词数先转 int**：有些年份超过 int 上限。直接转 Double，与 TimeSeries 的 value 类型一致。

本文假设使用课程有效数据，不额外设计通用 CSV 解析器或损坏数据修复。对同一个词同一年用 put 保存，不擅自把重复行当成新贡献累加。

### 5.6 验证

这一步先在 IntelliJ 中执行 Build → Build Project，确认新增字段和构造器能够编译。

**Main 还没有使用 NGramMap，所以现在重启网页不会自动验证文件解析。** 查询方法也还没完成，不要运行完整 NGramMapTest。第 6 步马上通过已知的 airport 数据同时验证“解析”和“查询”，如果出现路径、数组下标或数字解析异常，再回到本步检查。

这里不额外建一个临时 main，也不把半成品查询方法接进网页。

不要使用 `readAllLines`、`readAllStrings` 把大文件一次性拆入内存；也不要在每次查询时重新读文件。

**想一想：** 外层 Map 负责回答什么问题？内层 TimeSeries 负责回答什么问题？

---

## 第 6 步：根据单词和年份查询出现次数

### 6.1 先规定预期

```java
map.countHistory("airport", 2007, 2008)
```

应得到：

```text
{2007=175702.0, 2008=173294.0}
```

查不存在的词，应得到空 TimeSeries，不返回 null。

给外部的结果必须是副本，不能把内部那条 history 原样交出去。

### 6.2 先加测试

在 TutorialTest 类中加入：

```java
@Test
public void testCountHistoryAndCopy() {
    main.NGramMap map = new main.NGramMap(
            "data/word_history_size3.csv", "data/year_history.csv");
    TimeSeries result = map.countHistory("airport", 2007, 2008);
    assertThat(result.years()).containsExactly(2007, 2008).inOrder();
    assertThat(result.data()).containsExactly(175702.0, 173294.0).inOrder();

    result.put(2007, -1.0);
    assertThat(map.countHistory("airport").get(2007)).isEqualTo(175702.0);
    assertThat(map.countHistory("no_such_word_123")).isEmpty();
    assertThat(map.countHistory("airport", 1400, 1410)).isEmpty();

    TimeSeries totals = map.totalCountHistory();
    totals.clear();
    assertThat(map.totalCountHistory()).isNotEmpty();
}
```

这里写 `main.NGramMap` 是为了不额外要求你加 import；如果喜欢，也可以导入后写 NGramMap。

先运行，当前返回 null，测试会失败。

### 6.3 改 `src/main/NGramMap.java`

**① 替换带年份范围的 countHistory：**

```java
public TimeSeries countHistory(String word, int startYear, int endYear) {
    TimeSeries history = wordHistories.get(word);
    if (history == null) {
        return new TimeSeries();
    }
    return new TimeSeries(history, startYear, endYear);
}
```

这里直接使用第 2 步做好的范围复制构造器。之前写的小功能，现在派上用场了。

**② 替换不带年份范围的 countHistory：**

```java
public TimeSeries countHistory(String word) {
    return countHistory(word, MIN_YEAR, MAX_YEAR);
}
```

知识点：两个方法名字相同、参数不同，叫方法重载。没有指定范围时，用课程允许的完整范围调用已有方法，不再重复写一套逻辑。

**③ 替换 totalCountHistory：**

```java
public TimeSeries totalCountHistory() {
    return new TimeSeries(totalCounts, MIN_YEAR, MAX_YEAR);
}
```

同样返回副本。

### 6.4 为什么不能直接 return history？

如果交出去的是内部对象：

```java
TimeSeries result = map.countHistory("airport");
result.clear();
```

就可能删掉 NGramMap 内部 airport 的记录。现在得到副本，删掉 result 不影响内部数据。

也不要误以为 `private final` 自动保护了一切：一旦把内部可变对象的引用返回出去，调用者仍能改里面的数据。

### 6.5 验证

- 运行新加的 `testCountHistoryAndCopy`。
- 单独运行原 `NGramMapTest.testCountHistory`。
- 其他涉及 weight 的测试暂时可能失败，下一步再做。

**想一想：** 为什么“返回空 TimeSeries”比“返回 null”更方便后面继续算比例？

---

## 第 7 步：从出现次数算出相对频率

### 7.1 不引入新数据结构，只组合已有方法

某个词在 2007 年出现 100 次，全年总词数为 10000：

```text
相对频率 = 100 / 10000 = 0.01
```

对每年都这样计算，就是：

```text
countHistory(...) ÷ totalCounts
```

第 4 步的 dividedBy 正是为这件事准备的。

### 7.2 先加测试

在 TutorialTest 类中加入：

```java
@Test
public void testWeightHistory() {
    main.NGramMap map = new main.NGramMap(
            "data/word_history_size3.csv", "data/year_history.csv");
    double total = map.totalCountHistory().get(2007);
    double expected = 175702.0 / total;
    assertThat(map.weightHistory("airport", 2007, 2007).get(2007))
            .isWithin(1e-15).of(expected);
    assertThat(map.weightHistory("no_such_word_123")).isEmpty();

    double expectedSum = (175702.0 + 697645.0) / total;
    TimeSeries sum = map.summedWeightHistory(
            java.util.List.of("airport", "request"), 2007, 2007);
    assertThat(sum.get(2007)).isWithin(1e-15).of(expectedSum);
    assertThat(map.summedWeightHistory(java.util.List.of())).isEmpty();
}
```

浮点数计算用允许误差的比较，不要求所有计算路径都逐位相同。

### 7.3 改 `src/main/NGramMap.java`

**① 替换带范围的 weightHistory：**

```java
public TimeSeries weightHistory(String word, int startYear, int endYear) {
    return countHistory(word, startYear, endYear).dividedBy(totalCounts);
}
```

这行依次做两件事：先查次数，再按同一年总词数相除。

**② 替换不带范围的 weightHistory：**

```java
public TimeSeries weightHistory(String word) {
    return weightHistory(word, MIN_YEAR, MAX_YEAR);
}
```

**③ 替换带范围的 summedWeightHistory：**

```java
public TimeSeries summedWeightHistory(Collection<String> words,
                                      int startYear, int endYear) {
    TimeSeries result = new TimeSeries();
    for (String word : words) {
        result = result.plus(weightHistory(word, startYear, endYear));
    }
    return result;
}
```

**④ 替换不带范围的 summedWeightHistory：**

```java
public TimeSeries summedWeightHistory(Collection<String> words) {
    return summedWeightHistory(words, MIN_YEAR, MAX_YEAR);
}
```

知识点：

- Collection 表示“一组单词”，这里能遍历就够了，不必限定一定是 ArrayList。
- 结果从空序列开始，每次加上一条单词的相对频率。
- plus 返回新对象，所以必须写 `result = result.plus(...)`；只调用而不接收结果，没有效果。
- 未知单词的次数为空，空序列相除仍为空，因此不必满代码判断 null。

### 7.4 验证

运行：

1. TutorialTest 全部测试，现在应有 6 个。
2. 原 `TimeSeriesTest` 全部。
3. 原 `NGramMapTest` 全部，包含大文件。

加载大文件需要一些时间；按官网要求应在合理时间完成，通常不应超过约一分钟，但具体受电脑影响。

到这里，项目的数据计算部分已经完成。清理 NGramMap 中失效的 TODO 注释。

**闭卷练习：** 只用自然语言解释 weightHistory 怎么得到结果。如果你能说成“找这个词的次数，再除以全年总次数”，说明你没有被方法名绕晕。

---

## 第 8 步：把真实结果接到 History (Text) 按钮

### 8.1 现在才需要看处理器

之前网页点按钮执行的是 DummyHistoryTextHandler，当然只显示演示文字。

我们现在新增一个处理器，做三件事：

```text
拿到用户输入 → 调用 NGramMap → 拼成文字
```

不需要你实现 HTTP。框架已经把输入包装成 `NgordnetQuery q`：

| 读取方式 | 含义 |
|---|---|
| `q.words()` | 用户输入的单词列表 |
| `q.startYear()` | 起始年份 |
| `q.endYear()` | 结束年份 |
| `q.k()` | 后续项目使用，4A 忽略 |

这个类使用 Java record，所以读取方法是 `q.words()`，不是 `q.getWords()`。

### 8.2 新增 `src/main/HistoryTextHandler.java`

完整内容如下：

```java
package main;

import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;

public class HistoryTextHandler extends NgordnetQueryHandler {
    private final NGramMap map;

    public HistoryTextHandler(NGramMap map) {
        this.map = map;
    }

    @Override
    public String handle(NgordnetQuery q) {
        StringBuilder result = new StringBuilder();
        for (String word : q.words()) {
            TimeSeries history = map.weightHistory(word, q.startYear(), q.endYear());
            result.append(word).append(": ").append(history).append('\n');
        }
        return result.toString();
    }
}
```

知识点只看四件事：

1. `extends NgordnetQueryHandler`：按框架要求做一个处理器。
2. 构造器接收 map，存进字段，之后查询都用它。
3. `@Override`：这里实现父类要求的 handle 方法。
4. StringBuilder 一段段追加文字，最后转成 String。

构造器中两个 map 不要弄混：

```java
this.map = map;
```

左边是当前处理器的字段，右边是外部传进来的参数。

### 8.3 为什么不用自己拼大括号和逗号？

TimeSeries 继承了 TreeMap 的 toString：

```text
{2000=0.01, 2001=0.02}
```

`append(history)` 会调用这个字符串表示。最终一行就是：

```text
cat: {2000=0.01, 2001=0.02}
```

注意格式：单词、冒号、一个空格、序列、换行。**最后一行也必须换行。**

未知词输出 `word: {}` 后跟换行；不自己输出“没有找到”之类提示，否则不符合题目格式。

也不要手动生成 JSON，父类已经会做。

### 8.4 先验证处理器，不急着开网页

现有 `tests/HistoryTextHandlerTest.java` 中 imports 和测试方法都被注释了。

现在：

1. 去掉包围 imports 的 `/*` 和 `*/`。
2. 去掉包围 `@Test` 方法的 `/*` 和 `*/`。
3. 保留普通的 `//` 注释即可。
4. 运行此测试，应通过。

也可以向 TutorialTest 再加一个空结果格式测试：

```java
@Test
public void testUnknownWordText() {
    main.NGramMap map = new main.NGramMap(
            "data/word_history_size3.csv", "data/year_history.csv");
    main.HistoryTextHandler handler = new main.HistoryTextHandler(map);
    browser.NgordnetQuery query = new browser.NgordnetQuery(
            java.util.List.of("missing"), 2000, 2010, 0);
    assertThat(handler.handle(query)).isEqualTo("missing: {}\n");
}
```

此时 TutorialTest 应有 7 个方法。

### 8.5 修改 `src/main/Main.java`

只替换 main 方法，其他常量和 static 初始化代码保留：

```java
public static void main(String[] args) {
    NGramMap map = new NGramMap(WORD_HISTORY_SIZE14377_FILE, YEAR_HISTORY_FILE);
    NgordnetServer hns = new NgordnetServer();

    hns.startUp();
    hns.register("history", new DummyHistoryHandler());
    hns.register("historytext", new HistoryTextHandler(map));

    System.out.println("Finished server startup! Visit http://localhost:4567/ngordnet_4a.html");
}
```

这一阶段故意保留 DummyHistoryHandler，所以绘图仍是演示，文字则已经接真实数据。

注意创建位置：

- NGramMap 在 main 中创建一次。
- 通过构造器交给 Handler。
- 不在每次 handle 时重新读文件。
- 不改成一个公开的 static 全局变量。

你暂时只需理解“Main 做好数据，然后交给处理器用”。以后听到“构造器依赖注入”，指的就是这种传递依赖的方式，不是这里又要求你学一门框架。

### 8.6 验证

1. 停止旧服务，重新运行 Main。
2. 打开网页，输入 `cat, dog`，年份 `2000–2010`。
3. 点 History (Text)。
4. 应显示两行真实相对频率，不再是演示提示。
5. 输入不存在的词，应显示 `词: {}`。

这里使用大文件，是因为小文件不包含 cat/dog 等任意查询词。不要查到空结果就立即判定算法错了，先确认文件里是否有这个词。

**想一想：** 如果每次点按钮都 new NGramMap，即使计算结果对，体验会有什么问题？

---

## 第 9 步：把真实结果接到 History 绘图按钮

### 9.1 只需要换数据，不需要学画图算法

打开 `demo/DummyHistoryHandler.java`，可以看到它最终把两份列表交给 Plotter：

```text
标签列表：[parabola, sine wave]
数据列表：[抛物线序列, 正弦波序列]
```

我们只需换成：

```text
标签列表：[cat, dog]
数据列表：[cat 的相对频率序列, dog 的相对频率序列]
```

两个列表相同下标必须对应，长度必须相同。

### 9.2 新增 `src/main/HistoryHandler.java`

完整内容如下：

```java
package main;

import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;
import browser.Plotter;
import org.knowm.xchart.XYChart;

import java.util.ArrayList;
import java.util.List;

public class HistoryHandler extends NgordnetQueryHandler {
    private final NGramMap map;

    public HistoryHandler(NGramMap map) {
        this.map = map;
    }

    @Override
    public String handle(NgordnetQuery q) {
        List<String> labels = new ArrayList<>();
        List<TimeSeries> histories = new ArrayList<>();

        for (String word : q.words()) {
            TimeSeries history = map.weightHistory(word, q.startYear(), q.endYear());
            if (!history.isEmpty()) {
                labels.add(word);
                histories.add(history);
            }
        }

        XYChart chart = Plotter.generateTimeSeriesChart(labels, histories);
        return Plotter.encodeChartAsString(chart);
    }
}
```

逐段解释：

1. 准备两个空列表。
2. 每个单词查一条相对频率序列。
3. 有数据时，标签和数据同步加入。
4. generateTimeSeriesChart 生成图表。
5. encodeChartAsString 将图片表示成字符串，框架再交给浏览器。

**Base64 暂时只记住：这里是图片的字符串表示，不要求你自己实现编码。**

空序列不添加曲线，避免绘图库拒绝空数据；全部为空时生成空图。这个选择属于显示层处理，不会向 NGramMap 添加假的零记录。

### 9.3 修改 `src/main/Main.java` 的一行

将：

```java
hns.register("history", new DummyHistoryHandler());
```

替换为：

```java
hns.register("history", new HistoryHandler(map));
```

删除不再使用的两个 import：

```java
import demo.DummyHistoryHandler;
import demo.DummyHistoryTextHandler;
```

HistoryHandler、HistoryTextHandler 与 Main 同在 main 包，不需要额外导入。

### 9.4 验证完整项目

1. 重跑所有本地测试。
2. 重启 Main，等服务实际开始监听。
3. 打开 `http://localhost:4567/ngordnet_4a.html`。
4. 输入 cat、dog 和年份范围，先点文字，再点绘图。
5. 图应是词频曲线，不是抛物线和正弦波。
6. 单独查询未知词，文字为空序列，绘图应为空图，而不是 HTTP 500。
7. 连续换几组单词，服务不应每次重新解析大文件。
8. 使用完停止服务，避免占用 4567 端口。

**做到这里，4A 的全部功能已经接完。** 你没有写服务器，也没有实现图片编码；你做的是数据存储、查询、运算，并使用现成工具显示结果。

---

## 第 10 步：对照检查，避免“看起来能跑”

### 10.1 文件清单

| 文件 | 最终状态 |
|---|---|
| `src/main/TimeSeries.java` | 完成范围构造、years、data、plus、dividedBy |
| `src/main/NGramMap.java` | 两个字段、读文件、全部查询方法完成 |
| `src/main/HistoryTextHandler.java` | 新增，输出符合格式 |
| `src/main/HistoryHandler.java` | 新增，输出图片字符串 |
| `src/main/Main.java` | 共享同一个 NGramMap，注册两个真实处理器 |
| `tests/TutorialTest.java` | 新增，7 个阶段测试 |
| `tests/HistoryTextHandlerTest.java` | 原测试解除注释 |

不要改 `browser/` 来掩盖数据层错误，也不用修改 HTML 才能完成 4A。

### 10.2 最常见错误与定位方向

| 现象 | 先检查 |
|---|---|
| 找不到 data 文件 | 运行工作目录是不是 proj4a |
| 网页 404 | static 是否相对于工作目录存在 |
| NumberFormatException | 分隔符、列下标、是否误读标题/解释 |
| 单词只剩最后一个年份 | 是否每行都覆盖了一条新的 TimeSeries |
| Y-Axis null | years/data 是否仍返回 null |
| 点击文字仍是提示 | Main 是否仍注册 DummyHistoryTextHandler |
| 图仍是正弦波 | Main 是否仍注册 DummyHistoryHandler |
| 多词相加一直为空 | 是否遗漏 `result = result.plus(...)` |
| 修改查询结果影响下次查询 | 是否返回内部对象而不是副本 |
| 4567 端口占用 | 上一次服务是否仍运行 |
| 文本测试只差一个字符 | 冒号后的空格、最后一个换行 |

### 10.3 还值得自己补的测试

- years/data 返回的列表被清空后，原 TimeSeries 不受影响。
- 两个空序列相加，得到空序列。
- 范围没有任何记录，得到空序列。
- 多个单词只有部分年份重叠，仍保留正确年份。
- 多次查询同一个词，结果不被之前的调用破坏。

不要只看网页“有没有一张图”，还要知道里面的数据是否正确。

---

## 第 11 步：合上代码，巩固你真正学过的东西

这部分不再增加代码。

### 11.1 七个问题

1. 为什么 TimeSeries 不需要再有一个 TreeMap 字段？
2. 为什么 NGramMap 要有两个字段，而不只存单词次数？
3. 调用 `a.plus(b)`，方法里的 this 和 ts 分别是谁？
4. 为什么 plus 处理缺失年份时可以临时使用 0，而 dividedBy 不能给缺失分母补 0？
5. 为什么返回内部对象，即使字段是 private final，也不安全？
6. 为什么相对频率可以用一行 `countHistory(...).dividedBy(totalCounts)` 完成？
7. 为什么 Handler 只应负责查询和输出，而不是每次读文件？

答案提示：

1. 它本身继承了 TreeMap 的存储与操作。
2. 原始次数需要配合年度总量，才能算比例。
3. this 是 a，ts 是 b。
4. 相加时缺失侧只提供零贡献；缺失分母违反方法约定，也不能凭空构造除数。
5. 外部拿到内部可变对象引用后，仍可调用 clear/put 等方法。
6. 底层已经把范围复制、逐年除法实现好了，上层只需组合。
7. 重复读取大文件既慢又浪费；初始化时读一次即可。

### 11.2 三个闭卷小任务

不要立刻看参考实现，自己做：

- 重写 plus，包含双方独有的年份，不修改输入。
- 重写 weightHistory，只调用已有方法。
- 画出一次网页文字查询经过的类和数据。

能独立完成这三件事，才比“复制后跑通”更接近掌握。

### 11.3 复杂度只先理解两个对比

- 初始化读文件：与记录总数有关，读一次后保存。
- 查询：先定位单词，再复制/计算那条序列，不应每次扫描全部文件。

TreeMap 的查找和插入通常 O(log n)，HashMap 在常见哈希假设下查单词期望 O(1)。但 countHistory 还要复制年份数据，所以不能把整个查询都叫 O(1)。

本文范围构造采用简单的全序列扫描，对选中的年份再 get/put；保守上界可写 O(n log(n+1))。先正确，再考虑优化。后面的 B 再系统分析复杂度，不在这里一次展开所有公式。

---

# 做完 A 后再读：通向 B/C/D 的自学路线

## 一、先纠正“4 个部分考察点一样”

| 部分 | 你实际要做什么 | 是否需要新能力 |
|---|---|---|
| 4A | 单词历年次数、比例、文字和图 | 刚才已完成 |
| 4B | 为 WordNet 写设计文档 | 需要学会从查询倒推结构 |
| 4C | 查单词的下位词和共同下位词 | 需要有向图、DFS/BFS、集合交集 |
| 4D | 在共同下位词中选指定年份最常用的 k 个 | 组合 A/C，增加统计与排名 |

**A 做完并不能自动代替图遍历学习。** 官网 C 对应图实现与遍历的课程内容；如果你还没上到那里，先补这一小块，而不是认为自己 A 没学会。

下面给你够用的过渡，不直接提供 C/D 完整代码。

---

## 二、4B 的第一步：看懂 WordNet 的节点是什么

A 中：一个单词对应一条年份数据。

C 中：需要表示词义之间“更一般 → 更具体”的关系。

例子：

```text
0: animal
   ├──→ 1: dog canine
   │       └──→ 3: poodle
   └──→ 2: cat feline
```

- 每个编号是一个节点，代表一个 synset，即一个词义对应的同义词集合。
- 一个节点可以有多个词，如 dog、canine。
- 一个词可能出现在多个节点中，表示它有不同词义。
- 箭头从较宽泛的词义指向更具体的下位词义。
- 查询 animal 时，包含起点本身以及沿箭头能到的全部节点中的词。

**不要把“一个字符串”直接当成图里的唯一节点。多义词会让这种设计出错。**

### 给 B 的六个问题

像 A 中先问“如何按单词找时间序列”一样，这次先问：

1. 给定单词，怎么找到包含它的所有节点 ID？
2. 给定节点 ID，怎么找到里面的所有词？
3. 给定节点 ID，怎么找到它直接指向的孩子？
4. 怎么找到所有能到达的后代节点？
5. 怎么把这些节点变成去重的单词集合？
6. 多个单词都满足时，怎么只留下共同结果？

你可能需要的三个索引形状：

```text
单词 → 一组节点 ID
节点 ID → 一组单词
节点 ID → 一组直接孩子 ID
```

自己决定使用哪些类、字段及集合，再解释每个字段解决哪个问题。不要先堆出一长串嵌套容器，再反过来找用途。

### B 的设计文档可以这样写

```text
Data：输入文件各列是什么，边方向是什么。
Data Structures：类、字段，及其对应的查询需求。
Algorithms：建图、单词查询、多词查询，必要时写伪代码。
Complexity：定义 V、E 等规模，再分析时间和空间。
Diagram：画一张小图和对象关系。
学习补充：为每个容易错的语义安排一个测试。
```

官网 B 有设计要求与概念检查；提交细节以当前页面为准。这个模板是思考辅助，不替代官方提交表单。

---

## 三、4C 的第一步：先在纸上走一遍图

### 3.1 这就是遍历，不是神秘算法

```text
准备一个“待处理节点”容器
准备一个“已经访问”集合
把所有起点放进待处理容器

只要还有待处理节点：
    取出 v
    如果已经访问过：跳过
    标记 v 已访问
    将它的直接孩子加入待处理容器

得到所有已访问节点
```

用栈取节点就是 DFS，用队列就是 BFS。这里仅求“能不能到”，两者都能用，不要求最短路径。

visited 避免重复扩展共享后代，也能防止意外环造成死循环。图即使无环，菱形结构仍然可能有共享后代，因此不能因为“无环”就忽略重复处理问题。

先用迭代写法更容易避免深递归的栈溢出。按邻接表和常见集合操作假设，最坏遍历时间 O(V+E)，额外空间 O(V)。

### 3.2 最容易误解：不从结果单词重新起跳

```text
0: animal
1: dog canine
2: canine tooth
3: molar

边：0→1，2→3
```

查 animal，只能沿 0→1，得到 animal、dog、canine。

不能因为发现 canine，就再去查 canine 的另一种词义 2，然后跳到 3，把 tooth、molar 加进来。

正确顺序：

```text
只用原始查询词找全部起点
→ 沿节点 ID 的边遍历
→ 收集节点里的词
```

中途不因为同名单词再换到其他词义分支。

### 3.3 多词查询：取词的交集，不是节点交集

两个查询词可能沿不同分支到达：

```text
节点 10: beetle
节点 20: beetle
```

节点 ID 没有交集，单词 beetle 仍是共同下位词。

所以流程应是：

```text
每个查询词分别求下位词集合
→ 对这些单词集合取交集
→ 去重、按 Java 默认字典序输出
```

不能先交节点 ID 再展开词。输入不一定只有两个词；只要某一个词完全不存在，共同结果就为空。

### 3.4 文件解析提醒

- synsets：ID、同义词列表、解释；解释可能带逗号，分割时可以限制次数。
- 同义词列表按空格拆；带下划线的词如 video_recording 仍是一个词。
- hyponyms：父 ID 后面跟孩子 ID。
- 同一个父 ID 可以在多行出现，要合并孩子，不能覆盖旧边。
- 不必假设 ID 连续；若使用数组，要有数据保证或容量策略。
- 不擅自转小写，输出按默认的区分大小写字典序。
- 和 A 一样：文件初始化读一次，不在每次查询时再读。
- 官网要求自己实现图，不导入现成图算法库；一种实现就够，不必先造很多图接口。

### 3.5 按顺序闯关，每关自己写测试

1. 自己写一个有向图：能加边、查看孩子。
2. 单起点求可达节点，包含起点。
3. 多起点求可达节点并集。
4. 读文件，建立单词/ID/孩子三类查询关系。
5. 单词查起点、走图、收集词。
6. 处理多义词，保证不从结果词重新起跳。
7. 多词取词集合交集。
8. 去重、字典排序，接到 HyponymsHandler。
9. 最后处理 AutograderBuddy。

C 的 Handler 与 A 的接法类似，不要重新学习一遍服务器。

### 3.6 纸笔练习

```text
0: entity
1: animal beast
2: dog canine
3: cat feline
4: canine tooth
5: molar
6: pet dog

边：0→1，1→2，1→3，4→5
```

先自己算，再看答案：

1. animal 的下位词？
2. canine 的起点和下位词？
3. animal、canine 的共同下位词？
4. animal、pet 的共同下位词？为什么节点不重合也可以有答案？

答案：

1. `[animal, beast, canine, cat, dog, feline]`。
2. 起点 2、4；结果 `[canine, dog, molar, tooth]`。
3. `[canine, dog]`。
4. `[dog]`；dog 分别存在于可达节点 2 和节点 6。

---

## 四、4D：只增加一条“统计并排名”的流水线

### 4.1 先分清 k 的意思

- k=0：返回 C 的全部共同下位词，忽略年份，不做词频过滤。
- k>0：先求共同下位词，统计年份范围内出现次数，选最多 k 个。

**k=0 不是“返回 0 个”。**

### 4.2 D 用原始次数，不用相对频率

```text
C 计算共同候选词
    ↓
对每个候选：countHistory(word, startYear, endYear)
    ↓
将这条序列的 values 相加，得到原始次数总和
    ↓
排除总次数为 0 的词
    ↓
选次数最高的最多 k 个
    ↓
将选中的词按字母顺序输出
```

先交集再排名，不能每个词分别 Top-K 后再交集。

例如共同候选：

```text
apple=100，zebra=90，banana=80，ghost=0
```

- k=2：`[apple, zebra]`。
- k=3：`[apple, banana, zebra]`，最后不是按频率排序。
- k=10：仍只有前三个，不补零次数词。
- k=0：全部候选，包括 ghost；这是纯 WordNet 语义。

官网允许频率并列时任意打破并列；自己加单词字典序作为第二比较条件可以让结果稳定，但不是额外的官方强制条件。

### 4.3 先排序，后考虑堆

第一版推荐：

1. 每个候选的总次数只算一次，保存分数。
2. 按分数降序排序。
3. 截取前 k 个有效候选。
4. 将选中的词重新按字母排序。

候选数 C，排序阶段 O(C log C)。还要加上查询年份和求和成本，不能只写排序复杂度。

实现正确后，可以考虑容量为 k 的最小堆，减少选择阶段的成本。不要为了“看起来高级”先把简单版写成复杂版。

尤其不要在 Comparator 每次比较时都重新查词频、求总和：比较会重复发生，应该预先计算。

### 4.4 本地 CD 骨架与你的 A 有一个区别

当前 `../proj4cd/src/main/NGramMap.java` 使用官方 StaffNGramMap，并提供返回 `TreeMap<Integer, Double>` 的 countHistory。

所以 D 可以直接遍历返回 Map 的 values 求和，不一定要复制你自己的 TimeSeries。官网推荐使用官方实现，避免把 A 的 bug 一起带入后续项目。

不要写成“将官方 TreeMap 直接赋给自定义 TimeSeries”。父类型对象不会自动变成子类型。

A/CD 都有 `main.NGramMap`，运行时要选对模块，避免 classpath 混入两个同名类。

### 4.5 AutograderBuddy 不是额外算法

本地骨架入口为：

```text
getHyponymsHandler(wordHistoryFile, yearHistoryFile, synsetFile, hyponymFile)
```

它只是让测评器不用浏览器，也能获得一个配置好的处理器。

按传入路径创建数据服务，再传进 Handler，和 Main 的做法相似。不能忽略这些参数，硬编码成本地固定大文件。

---

## 五、独立通关检查表与思考题

### 必须自己覆盖的测试

| 部分 | 至少验证这些 |
|---|---|
| 图 | 单点、链、分叉、共享后代、多起点、重复边 |
| C | 多义词、不同节点同一个词、不跨词义重新起跳、未知词、多于两个输入词 |
| D | k=0、k=1、k 过大、零次数、年份改变排名、先交集后排名、最终字母序 |
| 接线 | Handler 读到参数、Buddy 使用传入文件、Main 注册正确名字 |

### 五道最后的思考题

1. 只有“ID→words”，没有“word→IDs”，每次按单词查起点会慢在哪里？
2. TreeSet 和 HashSet 各适合哪一步？是否必须所有中间集合始终有序？
3. 一个没有 NGrams 记录但存在于 WordNet 的词，在 k=0 时应不应该出现？
4. 为什么按“相对频率之和”排名，可能与按“次数之和”排名不同？
5. 为什么分别对每个查询词取 Top-K，再交集，会漏掉正确结果？

答案提示：

1. 可能每次扫描全部节点和单词，反向索引避免重复扫描。
2. HashSet 适合去重、交集；TreeSet 可同时维持排序；也可以无序计算，最后一次排序。
3. 应该，k=0 不按词频过滤。
4. 不同年份总词数不同，会改变各年的权重。
5. 某个共同词可能不是单个集合的前 k，却是共同集合里最好的词。

### 抓出“D 错用相对频率”的小数据

```text
2000：a=100，b=1，总词数=1000
2001：a=0，b=2，总词数=2
```

在这两个年份中：

- 次数之和：a=100，b=3，选 a。
- 相对频率之和：a=0.1，b=1.001，错误地选 b。

这是为测试设计的小样本，不是课程真实记录。

### 什么时候可以自己去做 B/C/D？

你不需要背完本文，但应该能做到：

- 不看答案写出 plus、dividedBy、weightHistory。
- 解释为什么查询返回副本、为什么初始化时只读一次文件。
- 画出三个 WordNet 索引，并解释各自用途。
- 纸上走通一次多起点 DFS/BFS。
- 区分词集合交集与节点交集。
- 说清 k=0 和 k>0 的不同，以及原始次数与相对频率的不同。

如果前两项会、后面不会，说明该补图知识，不是你之前的 A 白做了。

---

## 来源、验证与边界

### 官方来源

- [Project 4A](https://sp26.datastructur.es/proj4/proj4a/)
- [Project 4B](https://sp26.datastructur.es/proj4/proj4b/)
- [Project 4C](https://sp26.datastructur.es/proj4/proj4c/)
- [Project 4D](https://sp26.datastructur.es/proj4/proj4d/)
- [数据说明](https://sp26.datastructur.es/proj4/proj4-data/)
- [测试说明](https://sp26.datastructur.es/proj4/proj4-testing/)
- [LLM 政策](https://sp26.datastructur.es/policies/#llm-policy)

A/C 正文和本地 A/CD 骨架已直接读取。B/D 正文抓取失败，相关设计栏目、排名、零次数与并列要求依据官网搜索结果核对。提交细节请以你打开的当前官网为准。

### 如何理解本文验证

教程代码可以在临时目录重建验证，不需要提前替换你的源文件。通过本地可见测试不等于官方隐藏测试、课程风格检查和完整浏览器端到端测试全部通过。

本教程的分阶段顺序优先帮助你理解：先运行一个失败测试，再完成对应功能，最后才接网页。不要把“最终代码能编译”误认为“每个中间阶段所有测试都应该通过”。

### 本版实际验证记录

已在临时目录从原始骨架开始，按第 1–9 步提取并逐步应用本文代码：

- 9 个阶段分别编译成功。
- 各阶段已完成部分的测试通过，没有要求尚未实现的功能提前通过。
- 最终通过反射执行 13 个带 @Test 的方法：TutorialTest 7 个、原 TimeSeriesTest 2 个、原 NGramMapTest 3 个、解除注释的原 HistoryTextHandlerTest 1 个。
- 包括 NGramMap 的大文件测试。
- 没有替换项目源文件，也没有修改你的参考教程 TUTORIAL_v8-v10.md。

本轮没有重新执行浏览器端到端验证；网页验收请按第 8、9 步进行。
