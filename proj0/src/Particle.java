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
        lifespan = -1;
    }

    public Color color() {
        if (flavor == ParticleFlavor.EMPTY) {
            return Color.BLACK;
        } else if (flavor == ParticleFlavor.SAND) {
            return Color.YELLOW;
        } else if (flavor == ParticleFlavor.BARRIER ) {
            return Color.GRAY;
        } else if (flavor == ParticleFlavor.WATER) {
            return Color.BLUE;
        } else if (flavor == ParticleFlavor.FOUNTAIN){
            return Color.cyan;
        } else if (flavor == ParticleFlavor.PLANT){
            return new Color(0,255,0);
        } else if (flavor == ParticleFlavor.FIRE) {
            return new Color(255,0,0);
        } else if (flavor == ParticleFlavor.FLOWER){
            return new Color(255,141,161);
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
    }

    public void burn(Map<Direction, Particle> neighbors) {
    }

    public void action(Map<Direction, Particle> neighbors) {
//        Modify the action method so that the logic is:
//          1.If the flavor of the current particle is EMPTY, return immediately.
        if (this.flavor == ParticleFlavor.EMPTY){
            return;//这边不能是return 0;因为action函数声明的返回的类型为void,意味着并不需要返回值
        }
//          2.If the flavor of the current particle is not BARRIER, call fall.
        if (this.flavor  != ParticleFlavor.BARRIER){
            fall(neighbors);
        }

        if (this.flavor == ParticleFlavor.WATER){
            flow(neighbors);
        }
    }
}