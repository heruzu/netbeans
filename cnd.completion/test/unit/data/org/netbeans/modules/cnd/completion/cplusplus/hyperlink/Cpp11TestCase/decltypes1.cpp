/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

namespace {
  template<typename T, typename U> 
  struct is_same 
  {
    static const bool value = false;
  };

  template<typename T>
  struct is_same<T, T>
  {
    static const bool value = true;
  };

  const int& foo(); 
  int i; 
  struct A { double x; };
  const A* a = new A(); 

  static_assert(is_same<decltype(foo()), const int&>::value,
                "type should be const int&");
  static_assert(is_same<decltype(i), int>::value,
                "type should be int");
  static_assert(is_same<decltype(a->x), double>::value,
                "type should be double");
  static_assert(is_same<decltype((a->x)), const double&>::value,
                "type should be const double&");
}