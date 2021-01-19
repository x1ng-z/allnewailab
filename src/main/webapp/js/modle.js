var modledata = {
    'id': '4000',
    'name': '功能测试',
    'status': 'enable',
    'varList': [],
    'testproject': {
        'projectid': 1,
        'modules': [
            {
                'id': "1",
                'type': 'input',
                'name': '输入点集合',
                'status': '1',
                'child': [{
                    'id': 2
                }],
                'inputproperty': [],
                'outputproperty': [
                    {'pin': 'input.pv', 'name': '磨机振动', 'value': 0},
                    {'pin': 'input.pv', 'name': '磨机振动', 'value': 0},
                    {'pin': 'input.pv', 'name': '磨机振动', 'value': 0},
                    {'pin': 'input.pv', 'name': '磨机振动', 'value': 0},
                    {'pin': 'input.pv', 'name': '磨机振动', 'value': 0},
                    {'pin': 'input.pv', 'name': '磨机振动', 'value': 0},
                    {'pin': 'input.pv', 'name': '磨机振动', 'value': 0},
                    {'pin': 'input.pv', 'name': '磨机振动', 'value': 0}],
                'top': 50,
                'left': 200
            },
            {
                'id': "2",
                'type': 'filter',
                'name': '滤波器',
                'status': '1',
                'child': [{
                    'id': 3
                }],
                'inputproperty': [{'pin': 'input.pv', 'name': '磨机振动', 'value': 0}],
                'outputproperty': [{'pin': 'input.pv', 'name': '磨机振动', 'value': 0}],
                'top': 178,
                'left': 180
            },
            {
                'id': "3",
                'type': 'customize',
                'name': '自定义',
                'status': '1',
                'child': [{
                    'id': 4
                }],
                'inputproperty': [{'pin': 'input.pv', 'name': '磨机振动', 'value': 0}],
                'outputproperty': [{'pin': 'input.pv', 'name': '磨机振动', 'value': 0}],
                'top': 305,
                'left': 135
            },
            {
                'id': "4",
                'type': 'mpc',
                'name': 'mpc',
                'status': '1',
                'child': [{
                    'id': 5
                }],
                'inputproperty': [{'pin': 'input.pv', 'name': '磨机振动', 'value': 0}],
                'outputproperty': [{'pin': 'input.pv', 'name': '磨机振动', 'value': 0}],
                'top': 499,
                'left': 330
            },
            {
                'id': "5",
                'type': 'output',
                'name': '输出点集合',
                'status': '1',
                'child': [],
                'inputproperty': [{'pin': 'input.pv', 'name': '磨机振动', 'value': 0}],
                'outputproperty': [],
                'top': 700,
                'left': 330
            }
        ]
    }
}
