# 工程简介
1.数据库修改
 modle表中添加   modletype
                filtermethod
                filteralphe
                filtercapacity
                kp
                ki
                kd
                customizepyname
                
 modlepins表中添加
                pindir引脚方向
                modlepropertyclazz模型属性类型
 
 
 modlepins中的resouce是是一个String的json，包含几种形式
 {resoure:opc}是指直接的数据源是opc
 {resoure:constant,value:10}是指常量
 {resource:modle,modleId:1,modlepinsId:2}存储包含引脚数据来源的模块  
 
 
 模块的编写说明：
 ##filter模块：
   输入引脚的匹配是根据reource的modleid和properyid进行匹配的
 输出则根据输入的引脚名称进行一一匹配，将滤波的输出结果写入到对应引脚上去
##pid模块
模块引脚有
kp（数据来源constant/modle）
ki（同上）
kd（同上）
pv（同上）
sp（同上）
mv（同上）由于含义dmvhigh/low 这个引脚的类型是mpcproperty的
ff（同上）
mvup
mvdown

# 延伸阅读

