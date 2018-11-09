/*
 * Copyright 2018 NPR
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.npr.rad;

public class Utils {

    /***
     * Method converts the eventTime field of a Rad event, in the format in which it is extracted from metadata i.e. "HH:mm:ss.SSS", to epoch time.
     * @throws ArrayIndexOutOfBoundsException and if any of the values is missing within the format
     * @throws NumberFormatException and if any of the values is not a valid number
     */
    public static long getTime(String eventTime) {
        long result = 0;
        String[] numbers = eventTime.split(":");
        result += (3600L * 1000L * Long.parseLong(numbers[0]));
        result += (60L * 1000L * Long.parseLong(numbers[1]));
        result += (long) (1000L * Float.parseFloat(numbers[2]));
        return result;
    }
}
