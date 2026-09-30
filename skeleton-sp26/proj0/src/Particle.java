import edu.princeton.cs.algs4.StdRandom;

import java.awt.*;
import java.util.Map;

public class Particle {
    public ParticleFlavor flavor;
    public int lifespan;

    public static final int PLANT_LIFESPAN = 150;
    public static final int FLOWER_LIFESPAN = 75;
    public static final int FIRE_LIFESPAN = 10;
    public static final Map<ParticleFlavor, Integer> LIFESPANS =
            Map.of(ParticleFlavor.FLOWER, FLOWER_LIFESPAN,
                   ParticleFlavor.PLANT, PLANT_LIFESPAN,
                   ParticleFlavor.FIRE, FIRE_LIFESPAN);

    public Particle(ParticleFlavor flavor) {
        this.flavor = flavor;
        //Task 9:
//        First, update the Particle.java constructor to set the lifespan based on the Particle’s flavor.
//        You’ll see that the default implementation sets the lifespan to -1.
//        Change the behavior so that if the flavor is PLANT, FLOWER, or FIRE,
//        the lifespan is set according to the variables at the top of the class.
//        Otherwise, the lifespan should remain -1. Use the LIFESPANS map for the cleanest code.
//        使用if语句来实现
        if (flavor == ParticleFlavor.PLANT || flavor == ParticleFlavor.FLOWER || flavor == ParticleFlavor.FIRE){
            this.lifespan = LIFESPANS.get(flavor);
        }else{
//            this.flavor = ParticleFlavor.EMPTY;
            //Task9中，在最后运行testLifespan的时候，由于此行代码漏写，导致测试不通过
            //当火焰粒子寿命耗尽的时候，应该为空，但由于漏写了这行代码，导致，火焰粒子未如期消失
            //但这串代码逻辑还是有问题的，应该Use the LIFESPANS map for the cleanest code.
//            this.flavor = ParticleFlavor.EMPTY只管这个粒子的创造，而不管粒子的生命周期，生命周期是由lifespan来管理的

            this.lifespan = -1;//这个应该是表示这个粒子永恒,不会随时间消失
        }
        //表达的逻辑是:根据粒子传入的粒子类型flavor,把对应的寿命(150、70或10)赋值给当前粒子的this.lifespan;
//        如果不是这些特定类型，寿命就保持为默认的-1
        //上方的代码中已经写了这个LIFESPANS这个方法了，里面涉及到了一个映射，当传入的粒子为FLOWER时候，方法会返回FLOWER_LIFESPAN，而FLOWER对应
        //的LIFE_SPAN为75
    }

    public Color color() {
        if (flavor == ParticleFlavor.EMPTY) {
            return Color.BLACK;
        }
        else if (flavor == ParticleFlavor.SAND) {
            return Color.YELLOW;
        }
        else if (flavor == ParticleFlavor.BARRIER ) {
            return Color.GRAY;
        }
        else if (flavor == ParticleFlavor.WATER) {
            return Color.BLUE;
        }
        else if (flavor == ParticleFlavor.FOUNTAIN){
            return Color.cyan;
        }
        else if (flavor == ParticleFlavor.PLANT){
            double ratio = (double) Math.max(0, Math.min(lifespan, PLANT_LIFESPAN)) / PLANT_LIFESPAN;
            int g = 120 + (int) Math.round((255 - 120) * ratio);
            return new Color(0, g, 0);
        }
        else if (flavor == ParticleFlavor.FIRE) {
            double ratio = (double) Math.max(0, Math.min(lifespan, FIRE_LIFESPAN)) / FIRE_LIFESPAN;
            int r = (int) Math.round(255 * ratio);
            return new Color(r, 0, 0);
        }
        else if (flavor == ParticleFlavor.FLOWER){
            double ratio = (double) Math.max(0, Math.min(lifespan, FLOWER_LIFESPAN)) / FLOWER_LIFESPAN;
            int r = 120 + (int) Math.round((255 - 120) * ratio);
            int g = 70 + (int) Math.round((141 - 70) * ratio);
            int b = 80 + (int) Math.round((161 - 80) * ratio);
            return new Color(r, g, b);
        }
        return Color.GRAY;
    }

    public void moveInto(Particle other) {
        other.flavor = this.flavor;
        other.lifespan = this.lifespan;
        this.flavor = ParticleFlavor.EMPTY;
        this.lifespan = -1;
    }

    public void fall(Map<Direction, Particle> neighbors) {
        //怎么实现这个粒子移动的效果
//        if (neighbors.get(Direction.DOWN)){
//
//        }
//        if (neighbors.isEmpty()){
//        }
//        由google给出来的结果
        Particle below = neighbors.get(Direction.DOWN);
        //所以它这个是要先拿到当前粒子的下方的例子，如果下方粒子位置为空的话，当前例子会调用moveInto method 来实现例子移动
        //extending question:why check the paticle below the current particle?
//        检查当前粒子下方的粒子主要有以下几个核心原因：
//        1. **模拟物理世界中的“重力”（Gravity）与碰撞体积**主要原因
//                真实物理世界中，重力使物体竖直向下加速。但在网格离散化模拟（Cellular Automata）中，一个格子一次只能容纳一个粒子。要让粒子向下移动，必须先确认下方是否有“物理空间”，否则直接覆盖就会抹除下方的物体（导致粒子凭空消失）。
        //如果不做校验，待会粒子越掉越少了
//        2. **状态驱动的先决条件（Precondition Check）**
//        在面向对象设计中，`moveInto` 是一个无条件的破坏性覆盖操作（直接修改目标属性）。为了保证网格状态的一致性，必须先做校验：只有当下方的格子是“真空”（`ParticleFlavor.EMPTY`）时，下落动作才合法。

//        3. **为更复杂的流体/物理逻辑提供优先级分支（Priority Fallback）**
//* **对于固体（如沙子）：** 优先尝试正向下落；如果下方被挡住，后续可能会进一步检查左下或右下（斜向下滚落）。
//* **对于液体（如水）：** 同样优先检查下方（正向下落）；如果下方不是空的，水才会进一步检查左右两侧（水平流动）。
//        正下方的检查是所有这些运动逻辑中最基础、优先级最高的第一判断步。
        if (below.flavor == ParticleFlavor.EMPTY){
            this.moveInto(below);
        }
    }

    public void flow(Map<Direction, Particle> neighbors) {
//        Goal:
//        It should choose one of the following three choices:
//            With 1/3 chance, don’t do anything.
//            With 1/3 chance, if the left neighbor is empty, moveInto it.
//            With 1/3 chance, if the right neighbor is empty, moveInto it.
//        knowledge:离散网络模拟
//        why are you want to design the 1/3 chance?
//        主要是为了解决离散网络模拟中的几个常见问题
//        避免方向偏置（Directional Bias）：
//          如果不加随机性，直接用固定的顺序（例如先判断左边、再判断右边），水堆积成堆时就会永远优先朝同一个方向滑落，整个水面看起来会一边倒，失去对称感。
//        避免瞬间拉平（Instant Teleportation / Viscosity Control）：
//          如果不保留“1/3 概率不动”，水只要有横向空隙就会以最高速度每帧必走一格。在离散网格里，这会让水看起来像没有阻力、瞬间铺开的像素块。加入不动的概率相当于人为模拟了液体的阻尼/粘滞性（Viscosity），让扩散过程呈现平缓连续的流动动画。
//        规避双向冲突与振荡（Oscillation）：
//          在没有复杂物理动量系统的情况下，如果两个相邻的水粒子同时强行做左右选择，容易出现反复对穿或行为割裂。引入静止状态作为缓冲，可以打散粒子之间的竞争节奏，让多颗水滴在容器底部汇聚时更自然地达成平缓的液面。
//      这个设计是基于人类对于自然中水流的观察得出的规律，最终，再想出来的算法实现，这涉及到了具体事物抽象的能力
        int choice = StdRandom.uniformInt(3);
        //1/3概率直接什么都不做
        if (choice == 0){
            return;
        }
        //1/3概率尝试先左流
        if (choice == 1){
            Particle left = neighbors.get(Direction.LEFT);
            if (left.flavor == ParticleFlavor.EMPTY){
                this.moveInto((left));
            }
        }
        //1/3概率尝试向右流
        if (choice == 2){
            Particle right = neighbors.get(Direction.RIGHT);
            if(right.flavor == ParticleFlavor.EMPTY){
                this.moveInto(right);
            }
        }
    }

    public void grow(Map<Direction, Particle> neighbors) {
//        Modify public void grow(Map<Direction, Particle> neighbors). It should pick from the following four choices:
//          1.With 10% chance, if the UP neighbor has flavor EMPTY, set the flavor of the up neighbor to the same flavor as the current particle.
//          2.With 10% chance, if the LEFT neighbor has flavor EMPTY, set the flavor of the LEFT neighbor to the same flavor as the current particle.
//          3.With 10% chance, if the RIGHT neighbor has flavor EMPTY, set the flavor of the RIGHT neighbor to the same flavor as the current particle.
//          4.With 70% chance do none of the above.
//        Make sure to set the neighbor’s lifespan based on the flavor of the current particle, e.g. if a PLANT grows into the UP position, it should have a lifespan of 150 as given in LIFESPANS.
        int choice = StdRandom.uniformInt(10);

//        if (choice == 0){
//            Particle up = neighbors.get(Direction.UP);
//            if (up.flavor == ParticleFlavor.EMPTY){
//                up.flavor = this.flavor;
//            }
//        }
//        if (choice == 1){
//            Particle left = neighbors.get(Direction.LEFT);
//            if (left.flavor == ParticleFlavor.EMPTY){
//                left.flavor = this.flavor;
//            }
//        }
//        if (choice == 2){
//            Particle left = neighbors.get(Direction.LEFT);
//            if (left.flavor == ParticleFlavor.EMPTY){
//                left.flavor = this.flavor;
//            }
//        }
        //使用if语句写这个逻辑速度太慢了

        //以下是使用switch语句实现选择逻辑
        //老版java写法

        switch (choice) {
            case 0:
                // 什么都不做
                break;
            case 1:
                Particle up = neighbors.get(Direction.UP);
                if (up.flavor == ParticleFlavor.EMPTY) {
                    up.flavor = this.flavor;
                }
                break;
            case 2:
                Particle left = neighbors.get(Direction.LEFT);
                if (left.flavor == ParticleFlavor.EMPTY) {
                    left.flavor = this.flavor;
                }
                break;
            case 3:
                Particle right = neighbors.get(Direction.RIGHT);
                if (right.flavor == ParticleFlavor.EMPTY) {
                    right.flavor = this.flavor;
                }
            //利用传统java中switch算法case不写break，会自动下落的机制来实现
            //不过我们这道题中，并不需要用这个机制，直接default:break就可以表示剩下的全部都是直接break不做任何操作
            default:
                break;//至于说会不会有别的情况，这是不可能的，因为choice数字是由随机范围整数决定的，整数的值不会出现超出范围的情况
//            但以上的代码还是不完全符合满分标准
//            文中hint写到:use the lifespans map for the cleanest code
        }

    }

    public void decrementLifespan(){
        //为什么要这么设计?
//        if (this.lifespan > 0){
//            this.lifespan -= 1;
//        } else if (this.lifespan == 0) {
//            this.flavor = ParticleFlavor.EMPTY;
//            this.lifespan = -1;
//        }
        //使用以上逻辑的代码，出现了testLifespan通过不了的情况，
        //显然，我们可以知道是代码逻辑上出现了问题，为什么使用else if就跑不通代码了呢?
        //题目的意思是当粒子循环decrement后，递减为0之后，粒子就消失为空了，
        //而顺序结构if刚好可以实现这个效果，在粒子寿命为0后，就直接为空了
        //但else if语句实现的话，当它循环到第10次，粒子寿命递减为0后，它还要进行下一轮循环的判断才能让粒子消失为空
//        顺序执行的 if： “递减”与“归零检查”在同一个周期内完成流水线结算。粒子寿命耗尽的那一刻，
//            当场完成状态转变（消散为 EMPTY），刚好经过 10 个周期彻底结束。
//        else if： 属于互斥分支。当递减分支命中后，归零分支在当期被短路跳过，
    //        导致归零的粒子必须多活一个周期的“幽灵状态”，在第 11 轮才能被清理，
    //        从而与测试预期的生命周期长度（10 轮）产生 1 轮的偏差。

        if (this.lifespan > 0){
            this.lifespan -= 1;
        }
        if (this.lifespan == 0) {
            this.flavor = ParticleFlavor.EMPTY;
            this.lifespan = -1;
        }
    }

    public void burn(Map<Direction, Particle> neighbors) {
//        Modify public void burn(Map<Direction, Particle> neighbors) such that it has the following behavior:
//        For each neighbor, if the neighbor is either PLANT or FLOWER, with 40% chance independently,
//        give that flavor ParticleFlavor.FIRE and set its lifespan to FIRE_LIFESPAN.
//        Then modify the action method to so that if the current particle is FIRE, it calls burn.
//        int target = StdRandom.uniformInt(10);
//        if(neighbors = ParticleFlavor.FLOWER)
//        if ()
        //遍历每一个邻居粒子
        for (Particle neighbor : neighbors.values()){
            if (neighbor == null){
//                return;不能写return，如果写的是return的话，那么只要找到为空的邻居，循环就中止了，这与我们想要burn的效果不符合
                continue;//使用continue的话，找到null粒子后，也只是会跳过当前这个为null的粒子位置，继续找其他相邻位置的粒子
            }
            //如果找到粒子了之后，再检查一下粒子是否为花或植物
            if (neighbor.flavor == ParticleFlavor.PLANT || neighbor.flavor == ParticleFlavor.FLOWER){
                if (StdRandom.uniformDouble() < 0.4){//StdRandom.uniformDouble()可以去除0-1之间的小数,以达到一个概率的效果
                    //比起直接使用uniformInt 得到0到10之间随机的一个数，然会使用switch()case语句来达到概率的效果
                    //代码上要简洁得不少,其实官方的方法也是用java的Random方法实现的，低层的原理都是一样的
                    neighbor.flavor = ParticleFlavor.FIRE;//不能使用==,这个符号只能用来比较数值大小，面对这类将某个对象变为某个对象的，要使用赋值符号=
                    neighbor.lifespan = FIRE_LIFESPAN;
                }
            }
        }
    }

    public void action(Map<Direction, Particle> neighbors) {
//        Modify the action method so that the logic is:
//          1.If the flavor of the current particle is EMPTY, return immediately.
        if (this.flavor == ParticleFlavor.EMPTY){
            return;//这边不能是return 0;因为action函数声明的返回的类型为void,意味着并不需要返回值
        }
//          2.If the flavor of the current particle is not BARRIER, call fall.
        if (this.flavor  != ParticleFlavor.BARRIER){
            fall(neighbors);//this is enabling gravity
        }

        if (this.flavor == ParticleFlavor.WATER){
            flow(neighbors);
        }
        if (this.flavor == ParticleFlavor.FLOWER || this.flavor == ParticleFlavor.PLANT){
            grow(neighbors);
        }
        if (this.flavor == ParticleFlavor.FIRE){
            burn(neighbors);
        }
    }
}