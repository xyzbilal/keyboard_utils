import 'package:flutter/material.dart';
import 'package:keyboard_utils_example/input_page.dart';
import 'package:keyboard_utils_example/main_page.dart';
import 'package:keyboard_utils_example/normal_page.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MainPage(),
    );
  }
}
