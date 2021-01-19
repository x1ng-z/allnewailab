create table algorithmfilter
(
    filterid            int auto_increment
        primary key,
    filtername          varchar(100) null comment '滤波方法：mvav(移动平均)和fodl(一阶滤波)',
    filteralphe         double       null comment '一阶滤波参数
',
    filtertime          int          null comment '移动平均所用参数
',
    backtodcstag        varchar(100) null comment '滤波反写的opc位号
',
    resource            varchar(100) null comment 'opc位号来源',
    referencepropertyid int          null comment '引用的算法属性id'
);

create table algorithmmodle
(
    modleid       int auto_increment
        primary key,
    algorithmName varchar(100)                          null comment '算法名称',
    updatetime    timestamp default current_timestamp() null
)
    comment '算法模型(视频和大数据模型)';

create table algorithmproperties
(
    propertyid          int auto_increment
        primary key,
    propertyName        varchar(100) null comment '属性名称(ch-zh)',
    property            varchar(100) null comment '属性',
    refrencealgorithmid int          null comment '引用的算法id',
    resource            varchar(100) null comment 'opc数据源',
    opctag              varchar(100) null comment '反写的opc位号',
    datatype            varchar(100) null comment '属性类型，value/image'
)
    comment '算法结果属性表';

create table company
(
    companyId    int auto_increment
        primary key,
    commenName   varchar(50)   null,
    companyOrder int           null,
    ff           int default 8 null,
    mv           int default 8 null,
    pv           int default 8 null
);

create table filter
(
    filterid       int auto_increment comment '滤波器主键'
        primary key,
    filtermethod   varchar(100) null comment '滤波方法：mvav(移动平均)和fodl(一阶滤波),nofilt无滤波',
    filteralphe    double       null comment '一阶滤波系数',
    filtercapacity int          null comment '移动平均滤波时间',
    refmodleid     int          null comment '模型id主键'
);

create table modle
(
    modleId            int auto_increment
        primary key,
    modleName          varchar(300)  null,
    controlAPCOutCycle int           null comment 'mpc 参数',
    predicttime_P      int           null comment 'mpc 参数',
    timeserise_N       int           null comment 'mpc 参数',
    controltime_M      int           null comment 'mpc 参数',
    modleEnable        int           null comment '模块使能',
    runstyle           int default 0 null comment 'mpc运行方式0-自动分配模式 1-手动分配模式',
    modletype          varchar(300)  null comment 'input/output/filter/mpc/pid',
    customizepyname    varchar(3000) null comment '自定义脚本名称',
    refprojectid       int           null comment '引用projectid'
)
    comment '控制模型';

create table modlepins
(
    modlepinsId         int auto_increment
        primary key,
    refmodleId          int                                       null,
    modleOpcTag         varchar(1000) default '''NULL'''          null comment '数据源引脚(opc位号)',
    modlePinName        varchar(100)  default '''NULL'''          null comment '引脚名称 pv1...',
    opcTagName          varchar(1000) default 'NULL'              null comment '数据源位号中文名称',
    resource            varchar(3000)                             null comment '数据源',
    Q                   double                                    null,
    dmvHigh             double                                    null,
    deadZone            double                                    null,
    funelinitValue      double                                    null,
    R                   double                                    null,
    dmvLow              double                                    null,
    referTrajectoryCoef double        default 0                   null comment '参考轨迹系数（柔化系数）',
    funneltype          varchar(100)                              null,
    pinEnable           int           default 1                   null comment '引脚使能位，一般用于pv是否启用',
    updateTime          timestamp     default current_timestamp() null,
    pintype             varchar(100)                              null comment 'pv sp ff mv pvenable....',
    tracoefmethod       varchar(100)                              null comment '柔化系数方法：
before
after',
    pindir              varchar(100)                              null comment '引脚方向，输入(input)还是输出(output)',
    modlepropertyclazz  varchar(300)                              null comment 'baseproperty/mpcproperty'
);

create table modlerespon
(
    modleresponId   int auto_increment
        primary key,
    refrencemodleId int             null comment '模型id',
    stepRespJson    varchar(300)    null,
    inputPins       varchar(50)     null comment '引脚名称：mvn ffn等n=1,2,3..8',
    outputPins      varchar(50)     null comment '引脚名称：pvn 等n=1,2,3..8',
    effectRatio     float default 1 null comment '作用比例
pv1 ef1
pv2 ef2
pv1 和pv2都收到mv1的影响，
pv1对mv1计算出的dmv1
pv2对mv1计算出的dmv2，
作用于mv1的pv1部分为dmv1*ef1/(ef1+ef2)
',
    constraint modlerespon_ibfk_1
        foreign key (refrencemodleId) references modle (modleId)
);

create index refrencemodleId
    on modlerespon (refrencemodleId);

create table modlesight
(
    modlesightid int auto_increment
        primary key,
    positiontop  double        null,
    positionleft double        null,
    childs       varchar(3000) null comment '子节点信息',
    refmodleid   int           null,
    parents      varchar(3000) null comment '父节点信息'
);

create table opcserveinfo
(
    opcserveid  int auto_increment
        primary key,
    opcuser     varchar(100) null,
    opcpassword varchar(100) null,
    opcip       varchar(100) null,
    opcclsid    varchar(150) null
)
    comment 'opcserver的信息';

create table opcverification
(
    tagid      int auto_increment
        primary key,
    tagName    varchar(100) null,
    tag        varchar(100) null comment 'opc验证位号',
    opcserveid int          null comment 'opc serve的id'
)
    comment 'opc验证位号表';

create table project
(
    projectid int auto_increment
        primary key,
    name      varchar(100) null comment '工程名称',
    runperiod double       null comment '运行周期
'
)
    comment '工程';

create table shockdetect
(
    pk_shockdetectid   int auto_increment
        primary key,
    pk_pinid           int          null comment '引用的过滤器主键id',
    backToDCSTag       varchar(100) null comment '有效幅值计算结果反写位号',
    opcresource        varchar(100) null comment '有效幅值opc反写位号源',
    dampcoeff          double       null comment '动态阻尼系数
',
    windowstime        int          null comment '窗口时间',
    filtercoeff        double       null comment '一阶滤波系数',
    enable             int          null comment '是否启用',
    filterbacktodcstag varchar(100) null comment '滤波后数据反写位号',
    filteropcresource  varchar(100) null comment '滤波数据反写位号opc源'
)
    comment '震荡检测';

