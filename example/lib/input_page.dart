import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:keyboard_utils_example/keyborad_bloc.dart';
import 'package:keyboard_utils_fork/keyboard_utils.dart';
import 'package:keyboard_utils_fork/keyboard_listener.dart'
    as keyboard_listener;
import 'package:keyboard_utils_fork/widgets.dart';

class InputPage extends StatefulWidget {
  const InputPage({super.key});

  @override
  State<InputPage> createState() => _InputPageState();
}

class _InputPageState extends State<InputPage> with WidgetsBindingObserver {
  KeyboardBloc _bloc = KeyboardBloc();

  KeyboardUtils _keyboardUtils = KeyboardUtils();

  // 软键盘高度
  double tempKeyBoardHeight = 0;

  double tempKeyBoardToRatio = 0;

  double tempKeyBoardToTop = 0;

  double tempKeyboardToBottom = 0;

  double lastResult = 0;

  late int tempId;

  double get tempSafeBottomHeight => MediaQuery.of(context).padding.bottom;

  double get tempSafeTopHeight => MediaQuery.of(context).padding.top;

  double get devicePixelRatio => MediaQuery.of(context).devicePixelRatio;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    tempId = _keyboardUtils.add(
      listener: keyboard_listener.KeyboardListener(
        willHideKeyboard: () {
          lastResult = tempSafeBottomHeight;
          print('软键盘高度 willHideKeyboard tempHeight=$tempKeyBoardHeight');
          setState(() {});
        },
        willShowKeyboard: (result) {
          tempKeyBoardHeight = result;
          print('软键盘高度 willShowKeyboard tempHeight=$tempKeyBoardHeight');
          // print('输入页面 willShowKeyboard 像素=$tempKeyBoardHeight');
          // tempKeyBoardToRatio = tempKeyBoardHeight / devicePixelRatio;
          // print('输入页面 willShowKeyboard 除以密度后=$tempKeyBoardToRatio');
          //
          // tempKeyBoardToTop = tempKeyBoardToRatio - tempSafeTopHeight;
          // print(
          //     '输入页面 willShowKeyboard 减去顶部状态栏安全高度($tempSafeTopHeight)=$tempKeyBoardToTop');
          // // tempKeyboardToBottom = tempKeyBoardToTop - tempSafeBottomHeight;
          // tempKeyboardToBottom = tempKeyBoardToTop + 8;
          // print(
          //     '输入页面 willShowKeyboard 减去底部安全高度($tempSafeBottomHeight)=$tempKeyboardToBottom');
          //
          // lastResult = tempKeyboardToBottom;

          lastResult=tempKeyBoardHeight;
          setState(() {});
        },
      ),
    );
    _bloc.start();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    _bloc.dispose();
    _keyboardUtils.unsubscribeListener(subscribingId: tempId);
    super.dispose();
  }

  @override
  void didChangeMetrics() {
    // final viewInsetsBottom = MediaQuery.of(context).viewInsets.bottom;
    // final paddingBottom = MediaQuery.of(context).padding.bottom;
    // print('软键盘 viewInsetsBottom=$viewInsetsBottom paddingBottom=$paddingBottom');
    // if (viewInsetsBottom > 0) {
    //   // 软键盘弹起
    //   print('软键盘 开启');
    // } else {
    //   // 软键盘收起
    //   print('软键盘 关闭');
    // }
    // setState(() {
    //   lastResult = viewInsetsBottom;
    // });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Keyboard Utils Sample'),
      ),
      resizeToAvoidBottomInset: true,
      body: buildSampleUsingKeyboardAwareWidget(),
      floatingActionButton: FloatingActionButton(
        onPressed: () {},
        child: Icon(Icons.add),
      ),
    );
  }

  /// 使用 KeyboardAware
  Widget buildSampleUsingKeyboardAwareWidget() {
    return Center(
      child: Column(
        children: <Widget>[
          Container(
            margin: const EdgeInsets.only(bottom: 30),
            child: Text('当前顶部安全距离：$tempSafeTopHeight'),
          ),
          Container(
            margin: const EdgeInsets.only(bottom: 30),
            child: Text('当前底部安全距离：$tempSafeBottomHeight'),
          ),
          Expanded(
            child: GestureDetector(
              onTap: () {
                SystemChannels.textInput.invokeMethod('TextInput.hide');
              },
              behavior: HitTestBehavior.translucent,
              child: Center(
                child: Text(
                  '软键盘是否开启: ${_keyboardUtils.isKeyboardOpen}\n'
                  '软键盘像素高度: $tempKeyBoardHeight\n'
                  '软键盘除以屏幕密度后: $tempKeyBoardToRatio\n'
                  '减去顶部($tempSafeBottomHeight)高度: $tempKeyBoardToTop\n'
                  '减去底部($tempSafeBottomHeight)高度: $tempKeyboardToBottom',
                ),
              ),
            ),
          ),
          Container(
            height: 50,
            padding: EdgeInsets.symmetric(horizontal: 16),
            child: TextField(
              // keyboardType: TextInputType.number,
              decoration: InputDecoration(
                hintText: '请输入',
              ),
            ),
          ),
          Container(
            height: lastResult,
          ),
        ],
      ),
    );
  }

  /// 使用原生监听
  Widget buildSampleUsingRawListener() {
    return Center(
      child: Column(
        children: <Widget>[
          TextField(),
          TextField(
            keyboardType: TextInputType.number,
          ),
          TextField(),
          SizedBox(
            height: 30,
          ),
          StreamBuilder<double>(
            stream: _bloc.stream,
            builder: (BuildContext context, AsyncSnapshot<double> snapshot) {
              return Text(
                  'is keyboard open: ${_bloc.keyboardUtils.isKeyboardOpen}\n'
                  'Height: ${_bloc.keyboardUtils.keyboardHeight}');
            },
          ),
        ],
      ),
    );
  }
}
