## 2018-12-1 ~ 2018-12-7

###  **实验** 

**实验意图与方向**：复现对抗判别网络ADDA(Adversarial Discriminative Domain Adaptation)(https://arxiv.org/abs/1809.03625)

**实验结果**:对抗训练的时候网络没有学到想要的东西
训练集：philips数据集 测试集：toshiba

| 模型 | 真阳性 | 真阴性 |
|:------:|:------:|:------:|
|LeNet|77.5%|68.2%|
|ADDA-LeNet|3.8%| 96.3%|
### **论文阅读**
**题目**：Multimodal Densenet

**出处**：arxiv

**作者**：Faisal Mahmood, Ziyun Yang, Thomas Ashley and Nicholas J. Durr

**背景**在医学图像处理，数据常常是多模态的，诊断检查可能依赖于来自病人病史、体格检查、器官水平医学成像、组织学分析和实验室研究的信息。即使是单纯的图像信息，多模态的图像信息也有助于提高检出率。

**数据集**：
ISIT-UMR Multimodal Polyp Classification Dataset：数据集包括3类息肉:15种锯齿状腺瘤，21种增生性病变，40种腺瘤，共76例。

Kvasir Landmark and Pathological ClassificationDataset.：数据集共7类，3类为标志物检测(z线、幽门、盲肠)，3类为病理发现(食管炎、息肉和溃疡性结肠炎)，1类为正常结肠黏膜。每个类使用了1000个图像，总共使用了7000个图像。

**网络结构**：
![图1](png/muti结构.png)

上图中网络输入分为两部分，一部分是RGB信息，另一部分是NBI信息，对于没有NBI信息的数据集，使用风格转换网络将RGB信息转换成NBI

**实验结果**：
数据集：深度信息+RGB

![图2](png/深度信息+RGB.png)

数据集：NBI+RGB

![图2](png/NBI+RGB.png)

## 2018-12-8 ~ 2018-12-19

###  **实验** 

**实验意图与方向**：通过图像风格转换机制尝试对不同风格的甲状腺超声图像进行转换，在转换后进行特征的对齐

**实验结果**：使用cycleGan训练10000轮后，部分图像转换效果一般，部分存在雪花、亮点等不清晰的地方

### **论文阅读**
**题目**：mixup: BEYOND EMPIRICAL RISK MINIMIZATION

**出处** ：ICLR2018

**作者**：Hongyi Zhang(MIT) Moustapha Cisse, Yann N. Dauphin, David Lopez-Paz(FAIR)

**背景**:深度神经网络存在两个个共性的特点
1. 对于训练数据取最小平均错误（经验风险最小化）
2. 优秀的神经网络规模与训练样本数目呈线性关系
然而，经典机器学习理论告诉我们，只要学习机（如神经网络）的规模不随着训练数据数量的增加而增加，那么ERM的收敛性就是可以得到保证的。其中，学习机的规模由参数数量来衡量。 
1. 即使在强正则化情况下，或是在标签随机分配的分类问题中，ERM 也允许大规模神经网络去记忆（而不是泛化）训练数据。
2. 神经网络使用ERM 方法训练后，在训练分布之外的样本（对抗样本）上验证时会极大地改变预测结果。
同时，在测试分布与训练数据略有不同时，ERM 方法也不具有良好的解释和泛化性能。因此，我们需要数据增强来提高泛化性能。
**贡献**：作者提出了一种简单且数据无关的数据增强方式，被称作 mixup 。

mixup通过结合原始数据，使用特征向量的线性插值导致相关标签的线性插值，来扩展训练分布。
![公式](png/公式1.png)

**个人理解**：mixup领域分布可以看作一种的数据增强方式用来增强了模型在训练样本之间的线性表现。因此，它可以很好地对抗噪声样本
![图1](png/代码1.png)

上图显示了mixup 在类与类之间提供了更平滑的过渡线来估计不确定性。
![图2](png/图1.png)

上图显示了两个神经网络（using ERM and mixup）在训练CIFAR-10 数据集上的平均表现。两个模型有着同样的结构，使用同样的步骤训练，在同样的训练数据中采样相同的点进行评估。使用mixup训练的模型在训练样本之间的模型预测和梯度模值更加稳定。

**实验效果**
![图3](png/图2.png)
![图4](png/图3.png)

## 2018-12-20 ~ 2018-12-26
###  **实验**
**实验意图与方向**：重新看了下之前的IBN-Net和SENet，思考下觉得是否可以联系在一起
***IBN-Net***：
IBN-Net在网络中添加了Instance Normalization，Instance Normalization一般应用与风格迁移网络中，它可以提升网络对于风格变化的鲁棒性。
IBN-Net结构如图所示：

![图一](png/IBN-net结构.png)

网络在ResNet的残差块中，将第一层1\times1卷积层之后的BN操作改为一半通道使用BN，一半通道使用IN。
***SE-Net***：
SE-Net使用SE模块考虑网络特征通道之间的关系。SE模块结构如下图所示：

![图二](png/SENet特征重标定.png)

首先是 Squeeze 操作，对于一个通道数为c_2的特征，SE模块顺着空间维度来进行特征压缩，将每个二维的特征通道变成一个实数，这个数代表每个通道的全局感受野。
然后是 Excitation 操作，为每个特征通道生成权重，然后通过乘法逐通道加权到先前的特征上。

**实验效果**：
网络模型结构如图：

![图三](png/SE-ibn网络结构.png)

| 模型 | 准确率 | 
|:------:|:------:|
|ResNet|92.5%|
|IBN-net|93.5%|
|SE-IBN-net|96.7%|

## 2019-1-2 ~ 2019-1-9
###  **实验**
将之前的SE-Net中求取权值的模块放到ResNet残差块的第一层1\times1卷积层的后面，对于权值大于平均值的通道，进行BN，小于0的进行IN。具体操作是：例如一个8通道的feature，将它复制变成两个，将其中一个的权值大于均值的通道置为零，另外一个将其中一个的权值小于于均值的通道置为零。在正则化后，将两个feature相加。

![网络结构](png/我的网络.png)

| 模型 | 准确率 | 
|:------:|:------:|
|ResNet|92.5%|
|IBN-net|93.5%|
|SE-IBN-net|96.7%|
|我的  | 92.6% |

### **论文阅读**
**题目**：Dynamic Channel Pruning:Feature Boosting and Suppression

**出处** ：ICLR2019

**作者**：Xitong Gao1,Yiren Zhao2,Lukasz Dudziak3,Robert Mullins4,Cheng-zhong Xu

**背景**:本文针对的问题是模型压缩。要使深度卷积神经网络更加精确，通常需要增加计算和内存资源。提出了一种新的特征增强与抑制(feature boost and suppression, FBS)方法，该方法可以在运行时对显著卷积通道进行预测放大，对不重要的卷积通道进行跳过。

**贡献**：卷积输出中的某些通道神经元可能会变得非常兴奋，而另一组图像从相同的通道中可能几乎不会引起响应。本文这种特征增强与抑制(feature boost and suppression, FBS)机制使得显著的信息可以自由流动，对于不重要的信息可以停止并跳过，其网络结构如图1所示

![图1](png/动态网络.png)

辅助组件(红色部分)根据输入特征预测每个输出通道的重要性，并相应地放大输出特征。此外，预测某些输出通道将被完全抑制(置为0)

**实验结果**：本文的实验可以对VGG加速5倍，ResNet加速2倍，但是正确率仅损失0.8%。


## 2019-1-9 ~ 2019-1-16

###  **实验**
将之前的SE-Net中求取权值的模块放到ResNet残差块的第一层1\times1卷积层的后面，对于权值大于中位数的通道，进行BN，小于中位数的进行IN。具体操作是：例如一个8通道的feature，将它复制变成两个，将其中一个的权值大于中位数的通道置为零，另外一个将其中一个的权值小于中位数的通道置为零。在正则化后，将两个feature相加。

| 模型 | 准确率 | 
|:------:|:------:|
|ResNet|92.5%|
|IBN-net|93.5%|
|SE-IBN-net|96.7%|
|mean方法  | 93.3% |
|medain | 92.2%|


### **论文阅读**
**题目**：Gather-Excite:Exploiting Feature Context in Convolutional Neural Networks

**出处** ：NIPS2018

**作者**：Jie Hu(Momenta)

**背景**: 图像空间上的上下文信息能够有效的增强网络的性能，使用简单的特征聚集方法可以有效的提高图像分类、分割的准确性。GENet在SENet研究的基础上，针对更好的提取上下文信息作了改进。

**网络结构**：GENet定义了Gather算子G和Excite算子E
G:它把神经元在给定的空间范围内的响应集合起来;G操作是用一次或多次一定size和stride的池化操作得到输出，
E：它把集合和原始输入都包含进来，从而产生一个新的张量，其维数与原始输入的维数相同。E操作将G的输入变换到原始维度的包含上下文信息的张量，接着直接点乘上原输入X。 
其网络结构如图1所示

![网络结构](png/GENet网络结构.png)

实验中，作者共提出两种网络：
无参数方法GE-$\theta^-$,即池化操作是平均池化。
有参数方法GE-$\theta$,即池化操作改为Depthwise Separable Convolution

结构如图所示：

![残差块](png/GE卷积块.png)

使用全图进行分类实验，图片来源于philips和Toshiba两个机型，实验关注于在不同机型间的差异

| 模型 | 准确率 | 
|:------:|:------:|
|VGG16  |84.67|
|VGG19 |88.5
|ResNet|92.5%|
|DenseNet|93.5%|

