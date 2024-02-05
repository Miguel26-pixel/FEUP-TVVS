package com.google.gson.stream;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.lang.NullPointerException;
import java.lang.IllegalArgumentException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;  
import java.io.StringWriter;
import java.io.Writer;
import com.google.gson.stream.MalformedJsonException;


import static com.google.gson.stream.JsonScope.DANGLING_NAME;
import static com.google.gson.stream.JsonScope.EMPTY_ARRAY;
import static com.google.gson.stream.JsonScope.EMPTY_DOCUMENT;
import static com.google.gson.stream.JsonScope.EMPTY_OBJECT;
import static com.google.gson.stream.JsonScope.NONEMPTY_ARRAY;
import static com.google.gson.stream.JsonScope.NONEMPTY_DOCUMENT;
import static com.google.gson.stream.JsonScope.NONEMPTY_OBJECT;

public class StudentTest {

    @Test
    public void testValueWithInfinity() throws IOException {
    StringWriter string1 = new StringWriter();
    JsonWriter jsonWriter = new JsonWriter(string1);
    jsonWriter.setLenient(false);

    try {
        jsonWriter.value(Double.POSITIVE_INFINITY);
        fail("Expected IllegalArgumentException for POSITIVE_INFINITY");
    } catch (IllegalArgumentException e) {
        assertEquals("Numeric values must be finite, but was Infinity", e.getMessage());
    }

    try {
        jsonWriter.value(Double.NEGATIVE_INFINITY);
        fail("Expected IllegalArgumentException for NEGATIVE_INFINITY");
    } catch (IllegalArgumentException e) {
        assertEquals("Numeric values must be finite, but was -Infinity", e.getMessage());
    }

    assertNotNull(jsonWriter.value(42));


    jsonWriter.setLenient(true);
    assertNotNull(jsonWriter.value(Double.POSITIVE_INFINITY));
    assertNotNull(jsonWriter.value(Double.NEGATIVE_INFINITY));
    assertNotNull(jsonWriter.value(Double.NaN));
    }

    @Test
    public void testValueWithNaN() throws IOException {
        StringWriter string1 = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(string1);
        jsonWriter.setLenient(false);
        try {
            jsonWriter.value(Double.NaN);
            fail("Expected IllegalArgumentException for NaN");
        } catch (IllegalArgumentException e) {
            assertEquals("Numeric values must be finite, but was NaN", e.getMessage());
        }

        assertNotNull(jsonWriter.value(42));

        jsonWriter.setLenient(true);
        assertNotNull(jsonWriter.value(42));
    }

    @Test
    public void testCloseThrow() throws IOException {
        StringWriter string1 = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(string1);
        jsonWriter.beginObject();

        try {
            jsonWriter.close();
            fail("Expected IOException for incomplete document");
        } catch (MalformedJsonException e) {
            fail("Unexpected MalformedJsonException");
        } catch (IOException e) {
            assertEquals("Incomplete document", e.getMessage());
        }
    }


    @Test
    public void testConstructorWithNullWriter() {
        try {
            JsonWriter json = new JsonWriter(null);
            fail("Expected NullPointerException for null Writer");
        } catch (NullPointerException e) {
            assertEquals("out == null", e.getMessage()); 
        }
    }

    @Test
    public void testConstructorWithValidWriter() {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        assertNotNull(jsonWriter);

    }


    @Test
    public void testDeleteClose() throws IOException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        jsonWriter.beginObject();
        jsonWriter.endObject();

        try {
            jsonWriter.close();
            jsonWriter.flush();
            fail("Expected IllegalStateException for incomplete document");
        } catch (IllegalStateException e) {
            assertEquals("JsonWriter is closed.", e.getMessage());
        }

    }


    @Test
    public void testWithoutClose() throws IOException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        jsonWriter.beginObject();
        jsonWriter.endObject();

        try {
            jsonWriter.flush();
            jsonWriter.flush();
            //fail("Expected IllegalStateException for incomplete document");
        } catch (IllegalStateException e) {
            assertEquals("JsonWriter is closed.", e.getMessage());
        }

    }

    @Test
    public void testGeneralException() throws IOException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        jsonWriter.beginObject();

        try {
            jsonWriter.close();
            fail("Expected IOException for incomplete document");
        } catch (Exception e) {
            assertEquals("Incomplete document", e.getMessage());
        }

    }


    @Test
    public void testNewlineWithNullIndent() throws IOException {
        
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        try {
            jsonWriter.setIndent(null); 
            jsonWriter.jsonValue("our");
            fail("Expected IOException for null ident");
        } catch (IOException e) {
            assertEquals("", writer.toString());
        } catch (NullPointerException e) {
            assertEquals("", writer.toString());
        }
    }

    @Test
    public void testNewlineWithNonNullIndent() throws IOException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        jsonWriter.setIndent("    "); 

        jsonWriter.jsonValue("o");
        assertEquals("o", writer.toString());
    }

    @Test
    public void testNewlineWithIndentation() throws IOException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        jsonWriter.setIndent("    "); 

        jsonWriter.jsonValue("our");
        assertEquals("our", writer.toString());
    }

    @Test
    public void testValueWithTrue() throws IOException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        jsonWriter.value(true);

        assertEquals("true", writer.toString());
    }

    @Test
    public void testValueWithFalse() throws IOException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        jsonWriter.value(false);

        assertEquals("false", writer.toString());
    }

    @Test
    public void testSetIndentMutation() throws IOException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        jsonWriter.setIndent("  ");

        jsonWriter.beginObject();
        jsonWriter.name("key").value("value");
        jsonWriter.endObject();

        String jsonString = writer.toString();
        assertEquals("{\n  \"key\": \"value\"\n}", jsonString);

        StringWriter writer2 = new StringWriter();
        JsonWriter jsonWriter2 = new JsonWriter(writer2);

        jsonWriter2.setIndent("");

        try {
            jsonWriter2.beginObject();
            jsonWriter2.name("key").value("value");
            jsonWriter2.endObject();
        } catch (IllegalStateException e) {
            assertEquals("JSON must have only one top-level value.", e.getMessage());
        }
    }

    @Test
    public void testMutationInPushMethod() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        Field stacksizeField = JsonWriter.class.getDeclaredField("stackSize");
        stacksizeField.setAccessible(true);

        Field stackField = JsonWriter.class.getDeclaredField("stack");
        stackField.setAccessible(true);

        Method pushMethod = JsonWriter.class.getDeclaredMethod("push", int.class);
        pushMethod.setAccessible(true);

        Method peekMethod = JsonWriter.class.getDeclaredMethod("peek");
        peekMethod.setAccessible(true);

        int[] stack = new int[1];
        stackField.set(jsonWriter, stack);

        stacksizeField.setInt(jsonWriter, 1);
        int stackSize = stacksizeField.getInt(jsonWriter);
        
        assertEquals(stackSize, ((int[]) stackField.get(jsonWriter)).length);
        
        pushMethod.invoke(jsonWriter, 1);

        assertEquals(stackSize * 2, ((int[]) stackField.get(jsonWriter)).length);
        assertEquals(stackSize + stackSize, ((int[]) stackField.get(jsonWriter)).length);

        Object resultObject = peekMethod.invoke(jsonWriter);

        int result = (int) (Integer) resultObject;

        assertEquals(result, 1);

        stacksizeField.setInt(jsonWriter, 0);

        try {
            resultObject = peekMethod.invoke(jsonWriter);
            result = (int) (Integer) resultObject;
            fail("Expected InvocationTargetException for 0 stacksize");
        } catch (IllegalStateException e) {
            //
        } catch (InvocationTargetException e) {
            //
        }


        int[] stack2 = new int[4];
        stackField.set(jsonWriter, stack2);

        stacksizeField.setInt(jsonWriter, 1);
        stackSize = stacksizeField.getInt(jsonWriter);
        
        assertNotEquals(stackSize, ((int[]) stackField.get(jsonWriter)).length);
        
        pushMethod.invoke(jsonWriter, 1);

        assertNotEquals(stackSize * 2, ((int[]) stackField.get(jsonWriter)).length);
        assertNotEquals(stackSize + stackSize, ((int[]) stackField.get(jsonWriter)).length);

        resultObject = peekMethod.invoke(jsonWriter);

        result = (int) (Integer) resultObject;

        assertEquals(result, 1);
    }



    @Test
    public void testBeforeValues() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        Method peekMethod = JsonWriter.class.getDeclaredMethod("peek");
        peekMethod.setAccessible(true);

        Method beforeValueMethod = JsonWriter.class.getDeclaredMethod("beforeValue");
        beforeValueMethod.setAccessible(true);

        Object resultObject = peekMethod.invoke(jsonWriter);

        int result = (int) (Integer) resultObject;

        try{
            beforeValueMethod.invoke(jsonWriter);
            resultObject = peekMethod.invoke(jsonWriter);
            result = (int) (Integer) resultObject;
            assertEquals(result, NONEMPTY_DOCUMENT);
        }catch(Exception e){
            //
        }

        jsonWriter.setLenient(false);

        try{
            beforeValueMethod.invoke(jsonWriter);
            fail("Expected IllegalStateException for null name");
        }catch(IllegalStateException e){
            //
        }catch(InvocationTargetException e){
            //
        }

        jsonWriter.setLenient(true);
        jsonWriter.beginArray();

        try{
            beforeValueMethod.invoke(jsonWriter);
            resultObject = peekMethod.invoke(jsonWriter);
            result = (int) (Integer) resultObject;
            assertEquals(result, NONEMPTY_ARRAY);
            beforeValueMethod.invoke(jsonWriter);
            resultObject = peekMethod.invoke(jsonWriter);
            result = (int) (Integer) resultObject;
            assertEquals(result, NONEMPTY_ARRAY);
        }catch(Exception e){
            //
        }
    }

    @Test
    public void testDanglingName() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        Method peekMethod = JsonWriter.class.getDeclaredMethod("peek");
        peekMethod.setAccessible(true);

        Method beforeValueMethod = JsonWriter.class.getDeclaredMethod("beforeValue");
        beforeValueMethod.setAccessible(true);

        Method beforeNameMethod = JsonWriter.class.getDeclaredMethod("beforeName");
        beforeNameMethod.setAccessible(true);

        Object resultObject = peekMethod.invoke(jsonWriter);

        int result = (int) (Integer) resultObject;

        try{
            beforeValueMethod.invoke(jsonWriter);
            resultObject = peekMethod.invoke(jsonWriter);
            result = (int) (Integer) resultObject;
            assertEquals(result, NONEMPTY_DOCUMENT);
        }catch(Exception e){
            //
        }

        jsonWriter.setLenient(true);
        jsonWriter.beginObject();

        try{
            resultObject = peekMethod.invoke(jsonWriter);
            result = (int) (Integer) resultObject;
            assertEquals(result, EMPTY_OBJECT);
            beforeNameMethod.invoke(jsonWriter);
            resultObject = peekMethod.invoke(jsonWriter);
            result = (int) (Integer) resultObject;
            assertEquals(result, DANGLING_NAME);
            beforeValueMethod.invoke(jsonWriter);
            resultObject = peekMethod.invoke(jsonWriter);
            result = (int) (Integer) resultObject;
            assertEquals(result, NONEMPTY_OBJECT);
        }catch(Exception e){
            //
        }
    }

    @Test
    public void testValueLenient() throws IOException, IllegalStateException {
        StringWriter string1 = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(string1);
        jsonWriter.setLenient(false);
        assertNotNull(jsonWriter.value(42));
        
        // Fix for mutants JIR_Ifle and JIR_Ifgt
        try {
            double positiveInfinity = Double.POSITIVE_INFINITY;
            if (!(!jsonWriter.isLenient() && (Double.isNaN(positiveInfinity) || Double.isInfinite(positiveInfinity))))  {
                fail("Expected IllegalArgumentException for POSITIVE_INFINITY");
            }
            jsonWriter.value(positiveInfinity);
        } catch (IllegalArgumentException e) {
            assertEquals("Numeric values must be finite, but was Infinity", e.getMessage());
        }

        try {
            double negativeInfinity = Double.NEGATIVE_INFINITY;
            if (!(!jsonWriter.isLenient() && (Double.isNaN(negativeInfinity) || Double.isInfinite(negativeInfinity)))) {
                fail("Expected IllegalArgumentException for NEGATIVE_INFINITY");
            }
            jsonWriter.value(negativeInfinity);
        } catch (IllegalArgumentException e) {
            assertEquals("Numeric values must be finite, but was -Infinity", e.getMessage());
        }

        try {
            double nanValue = Double.NaN;
            if (!(!jsonWriter.isLenient() && (Double.isNaN(nanValue) || Double.isInfinite(nanValue)))) {
                fail("Expected IllegalArgumentException for NaN");
            }
            jsonWriter.value(nanValue);
        } catch (IllegalArgumentException e) {
            assertEquals("Numeric values must be finite, but was NaN", e.getMessage());
        }

        jsonWriter.setLenient(true);
        assertNotNull(jsonWriter.value(42));
        assertNotNull(jsonWriter.value(Double.POSITIVE_INFINITY));
        assertNotNull(jsonWriter.value(Double.NEGATIVE_INFINITY));
        assertNotNull(jsonWriter.value(Double.NaN));
    }


    @Test
    public void testStaticReplacement() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        StringWriter string1 = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(string1);

        Field replacementField = JsonWriter.class.getDeclaredField("REPLACEMENT_CHARS");
        replacementField.setAccessible(true);

        String[] expectedReplacementChars = new String[128];
        for (int i = 0; i <= 0x1f; i++) {
            expectedReplacementChars[i] = String.format("\\u%04x", (int) i);
        }

        char[] replacementChars = {'"', '\\', '\t', '\b', '\n', '\r', '\f'};
        String[] replacements = {"\\\"", "\\\\", "\\t", "\\b", "\\n", "\\r", "\\f"};

        for (int i = 0; i < replacementChars.length; i++) {
            expectedReplacementChars[replacementChars[i]] = replacements[i];
        }

        String[] actualReplacementChars = (String[]) replacementField.get(jsonWriter);

        assertArrayEquals(expectedReplacementChars, actualReplacementChars);
    }

    @Test
    public void testCloseCompleteDocument() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);

        Field stackField = JsonWriter.class.getDeclaredField("stack");
        stackField.setAccessible(true);

        Field stacksizeField = JsonWriter.class.getDeclaredField("stackSize");
        stacksizeField.setAccessible(true);

        jsonWriter.beginObject();
        jsonWriter.name("key").value("value");
        jsonWriter.endObject();

        int[] stack = (int[]) stackField.get(jsonWriter);
        int stackSize = (int) stacksizeField.getInt(jsonWriter);

        try {
            assertTrue(stackSize <= 1);
            assertTrue(stack[stackSize - 1] == NONEMPTY_DOCUMENT);
            assertNotEquals(stackSize - 1, stackSize--);
            jsonWriter.close();
        } catch (IOException e) {
            fail("Closing a complete document should not throw an exception");
        }
    }

    @Test
    public void testCloseIncompleteDocument() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);

        Field stackField = JsonWriter.class.getDeclaredField("stack");
        stackField.setAccessible(true);

        Field stacksizeField = JsonWriter.class.getDeclaredField("stackSize");
        stacksizeField.setAccessible(true);

        jsonWriter.beginObject();
        jsonWriter.name("key").value("value");

        int[] stack = (int[]) stackField.get(jsonWriter);
        int stackSize = (int) stacksizeField.getInt(jsonWriter);

        try {
            assertTrue(stackSize > 1);
            assertTrue(stack[stackSize - 1] != NONEMPTY_DOCUMENT);
            assertNotEquals(stackSize - 1, stackSize--);
            jsonWriter.close();
            fail("Closing an incomplete document should throw an exception");
        } catch (IOException e) {
            assertEquals("Incomplete document", e.getMessage());
        }
    }

    @Test
    public void testCloseEmptyDocument() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);

        Field stackField = JsonWriter.class.getDeclaredField("stack");
        stackField.setAccessible(true);

        Field stacksizeField = JsonWriter.class.getDeclaredField("stackSize");
        stacksizeField.setAccessible(true);

        int[] stack = (int[]) stackField.get(jsonWriter);
        int stackSize = (int) stacksizeField.getInt(jsonWriter);

        try {
            assertTrue(stackSize == 1);
            assertTrue(stack[stackSize - 1] != NONEMPTY_DOCUMENT);
            assertNotEquals(stackSize - 1, stackSize--);
            jsonWriter.close();
            fail("Closing an empty document should throw an exception");
        } catch (IOException e) {
            assertEquals("Incomplete document", e.getMessage());
        }
    }

    @Test
    public void testStringNoReplacement() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        jsonWriter.setHtmlSafe(false);

        Method stringMethod = JsonWriter.class.getDeclaredMethod("string", String.class);
        stringMethod.setAccessible(true);

        stringMethod.invoke(jsonWriter, "Hello, World!");

        assertEquals("\"Hello, World!\"", stringWriter.toString());
    }

    @Test
    public void testStringWithHtmlReplacement() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        jsonWriter.setHtmlSafe(true);

        Method stringMethod = JsonWriter.class.getDeclaredMethod("string", String.class);
        stringMethod.setAccessible(true);

        stringMethod.invoke(jsonWriter, "This is <b>HTML</b>.");

        assertEquals("\"This is \\u003cb\\u003eHTML\\u003c/b\\u003e.\"", stringWriter.toString());    }

    @Test
    public void testStringWithSpecialCharacterReplacement() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        jsonWriter.setHtmlSafe(false);

        Method stringMethod = JsonWriter.class.getDeclaredMethod("string", String.class);
        stringMethod.setAccessible(true);

        stringMethod.invoke(jsonWriter, "Line 1\nLine 2");

        assertEquals("\"Line 1\\nLine 2\"", stringWriter.toString());
    }

    @Test
    public void testStringWithUnicodeReplacement() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        jsonWriter.setHtmlSafe(false);

        Method stringMethod = JsonWriter.class.getDeclaredMethod("string", String.class);
        stringMethod.setAccessible(true);

        stringMethod.invoke(jsonWriter, "This is a multiline\u2028string.");

        assertEquals("\"This is a multiline\\u2028string.\"", stringWriter.toString());
    }

    @Test
    public void testNullValue() throws IOException {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);

        jsonWriter.nullValue();

        String result = stringWriter.toString();

        String expectedOutput = "null";

        assertEquals(expectedOutput, result);
    }

    @Test
    public void testSerializeNullsTrue() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);

        Field defferedField = JsonWriter.class.getDeclaredField("deferredName");
        defferedField.setAccessible(true);


        jsonWriter.nullValue();

        String resultString = stringWriter.toString();
        assertEquals(resultString, "null");
    }


    @Test
    public void testSerializeNullsFalse() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);

        Field defferedField = JsonWriter.class.getDeclaredField("deferredName");
        defferedField.setAccessible(true);

        jsonWriter.setSerializeNulls(false);

        jsonWriter.nullValue();

        String resulty = stringWriter.toString();

        String expectedOutput = "null";

        assertEquals(resulty, expectedOutput);

    }

    @Test
    public void testStringNoplacement() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);

        Method stringMethod = JsonWriter.class.getDeclaredMethod("string", String.class);
        stringMethod.setAccessible(true);

        String input = "abc"; // No special characters, no replacements

        stringMethod.invoke(jsonWriter, input);

        String resultString = stringWriter.toString();
        assertEquals("\"" + input + "\"", resultString);
    }

    @Test
    public void testStringplacement() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);

        Method stringMethod = JsonWriter.class.getDeclaredMethod("string", String.class);
        stringMethod.setAccessible(true);

        String input = "\t"; // Special character with replacement

        stringMethod.invoke(jsonWriter, input);

        String resultString = stringWriter.toString();
        assertEquals("\"\\t\"", resultString);
    }

    @Test
    public void testStringWithplacement() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);

        Method stringMethod = JsonWriter.class.getDeclaredMethod("string", String.class);
        stringMethod.setAccessible(true);


        stringMethod.invoke(jsonWriter, "\u2028");

        String resultString = stringWriter.toString();
        assertEquals("\"\\u2028\"", resultString);
        
        stringWriter.getBuffer().setLength(0);


        stringMethod.invoke(jsonWriter, "\u2029");

        resultString = stringWriter.toString();
        assertEquals("\"\\u2029\"", resultString);
        
        stringWriter.getBuffer().setLength(0);

        stringMethod.invoke(jsonWriter, "Test string with ASCII characters: !@#$%^&*()");

        resultString = stringWriter.toString();
        assertEquals("\"Test string with ASCII characters: !@#$%^&*()\"", resultString);
        
        stringWriter.getBuffer().setLength(0);

        stringMethod.invoke(jsonWriter, "A");

        resultString = stringWriter.toString();
        assertEquals("\"A\"", resultString);
        
        stringWriter.getBuffer().setLength(0);

        stringMethod.invoke(jsonWriter, "abc\"\u2028\u2029");

        resultString = stringWriter.toString();
        assertEquals("\"abc\\\"\\u2028\\u2029\"", resultString);
        
        stringWriter.getBuffer().setLength(0);

    }

    @Test
    public void testSetIndentNull() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);

        Field indentField = JsonWriter.class.getDeclaredField("indent");
        indentField.setAccessible(true);

        String inicial = (String) indentField.get(jsonWriter);

        jsonWriter.setIndent("");

        String result = (String) indentField.get(jsonWriter);
        assertEquals(inicial, result);

    }

    @Test
    public void testIndentLessThan() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        Field indentField = JsonWriter.class.getDeclaredField("indent");
        indentField.setAccessible(true);

        jsonWriter.setIndent("is"); 
        String inicial = (String) indentField.get(jsonWriter);
        if(inicial.length() < 0 ) {
            fail("Expected not to be true");
        }
        String result = (String) indentField.get(jsonWriter);
        assertEquals(inicial, result);
    }

    @Test
    public void testLenientGreatThan() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        Field lenientField = JsonWriter.class.getDeclaredField("lenient");
        lenientField.setAccessible(true);

        jsonWriter.setLenient(false);

        try {
            Boolean lenient = (Boolean) lenientField.get(jsonWriter);
            double value2 = 42;

            assertEquals(lenient, false);

            jsonWriter.beginArray();  // Start an array

            if (value2 < Double.POSITIVE_INFINITY) {
                jsonWriter.value(value2);
            }

            if (value2 > Double.NEGATIVE_INFINITY) {
                jsonWriter.value(value2);
            }

            if (value2 >= Double.POSITIVE_INFINITY) {
                jsonWriter.value(value2);
            }

            if (value2 <= Double.NEGATIVE_INFINITY) {
                jsonWriter.value(value2);
            }

            jsonWriter.endArray();  // End the array
        } catch (IllegalArgumentException e) {
            // Handle exception
        }

        jsonWriter.setLenient(true);

        try {
            Boolean lenient = (Boolean) lenientField.get(jsonWriter);
            double value2 = 42;

            assertEquals(lenient, true);

            jsonWriter.beginArray();  // Start an array

            if (value2 < Double.POSITIVE_INFINITY) {
                jsonWriter.value(value2);
            }

            if (value2 > Double.NEGATIVE_INFINITY) {
                jsonWriter.value(value2);
            }

            if (value2 >= Double.POSITIVE_INFINITY) {
                jsonWriter.value(value2);
            }

            if (value2 <= Double.NEGATIVE_INFINITY) {
                jsonWriter.value(value2);
            }

            if (value2 < Double.NaN) {
                jsonWriter.value(value2);
            }

            if (value2 > Double.NaN) {
                jsonWriter.value(value2);
            }

            jsonWriter.endArray();  // End the array
        } catch (IllegalArgumentException e) {
            // Handle exception
        }
    }
    
    @Test
    public void testWTClose() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        Field outField = JsonWriter.class.getDeclaredField("out");
        outField.setAccessible(true);

        Field stacksizeField = JsonWriter.class.getDeclaredField("stackSize");
        stacksizeField.setAccessible(true);

        Field stackField = JsonWriter.class.getDeclaredField("stack");
        stackField.setAccessible(true);

        Method replaceTopMethod = JsonWriter.class.getDeclaredMethod("replaceTop", int.class);
        replaceTopMethod.setAccessible(true);

        int[] stack = new int[1];
        stack[0] = NONEMPTY_DOCUMENT;
        stackField.set(jsonWriter, stack);

        stacksizeField.setInt(jsonWriter, 1);
        int stackSize = stacksizeField.getInt(jsonWriter);


        StringWriter out = (StringWriter) outField.get(jsonWriter);
        int size = stackSize;
        if (size > 1 || size == 1 && stack[size - 1] != NONEMPTY_DOCUMENT) {
            fail("Closing a empty document should throw an exception");
        }
        stackSize = 0;
        try{
            jsonWriter.value(32);
            fail("Closing a closed document should throw an exception");
        }catch(IllegalStateException e) {
            //assertEquals("JsonWriter is closed.", e.getMessage());
        }
    }

    @Test
    public void testWClose() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        Field outField = JsonWriter.class.getDeclaredField("out");
        outField.setAccessible(true);

        Field stacksizeField = JsonWriter.class.getDeclaredField("stackSize");
        stacksizeField.setAccessible(true);

        Field stackField = JsonWriter.class.getDeclaredField("stack");
        stackField.setAccessible(true);

        Method replaceTopMethod = JsonWriter.class.getDeclaredMethod("replaceTop", int.class);
        replaceTopMethod.setAccessible(true);

        int[] stack = new int[1];
        stack[0] = NONEMPTY_DOCUMENT;
        stackField.set(jsonWriter, stack);

        stacksizeField.setInt(jsonWriter, 1);
        int stackSize = stacksizeField.getInt(jsonWriter);
        jsonWriter.close();
        try{
            jsonWriter.value(32);
            fail("Closing a closed document should throw an exception");
        }catch(IllegalStateException e) {
            assertEquals("JsonWriter is closed.", e.getMessage());
        }
    }


    @Test
    public void testGT392() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        Field stacksizeField = JsonWriter.class.getDeclaredField("stackSize");
        stacksizeField.setAccessible(true);

        Field stackField = JsonWriter.class.getDeclaredField("stack");
        stackField.setAccessible(true);

        int[] stack = new int[1];
        stack[0] = NONEMPTY_DOCUMENT;
        stackField.set(jsonWriter, stack);

        stacksizeField.setInt(jsonWriter, 0);
        int stackSize = stacksizeField.getInt(jsonWriter);

        try{
            assertTrue(stackSize == 0);
            jsonWriter.name("close");
            fail("JsonWriter is closed.");
        }catch(IllegalStateException e) {
            assertEquals("JsonWriter is closed.", e.getMessage());
        }

        stacksizeField.setInt(jsonWriter, 1);
        stackSize = stacksizeField.getInt(jsonWriter);
        assertTrue(stackSize >= 0);

        try{
            jsonWriter.name("close");
        }catch(IllegalStateException e) {
            fail("JsonWriter is closed.");
        }
    }

    //558GE

    @Test
    public void testEQ495() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        jsonWriter.beginObject();
        double value = 42;

        assertTrue(value <= Double.POSITIVE_INFINITY);
        assertTrue(value >= Double.NEGATIVE_INFINITY);

        try{
            jsonWriter.name("key").value(value);
            jsonWriter.endObject();
            //fail("Expected IllegalArgumentException for infinite argument / 2.");
        }catch(IllegalArgumentException e) {
            fail("Not Expected IllegalArgumentException for infinite argument.");
        }

    }


    @Test
    public void testBooleanValue() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        jsonWriter.beginObject();

        jsonWriter.name("key1").value(true);
        assertEquals("{\"key1\":true", writer.toString());

        jsonWriter.name("key2").value(false);
        assertEquals("{\"key1\":true,\"key2\":false", writer.toString());

        jsonWriter.endObject();
        jsonWriter.close();
        assertEquals("{\"key1\":true,\"key2\":false}", writer.toString());
    }

    @Test
    public void testLastLessThanLength() throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException, IOException, InvocationTargetException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        Field outField = JsonWriter.class.getDeclaredField("out");
        outField.setAccessible(true);
        Method stringMethod = JsonWriter.class.getDeclaredMethod("string", String.class);
        stringMethod.setAccessible(true);

        StringWriter stringWriter = new StringWriter();
        outField.set(jsonWriter, stringWriter);

        String testValue = "abc";
        stringMethod.invoke(jsonWriter, testValue);

        String expectedOutput = "\"abc\"";
        assertEquals(expectedOutput, stringWriter.toString());
    }

    @Test
    public void testLastGreaterThanLength() throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException, IOException, InvocationTargetException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        Field outField = JsonWriter.class.getDeclaredField("out");
        outField.setAccessible(true);
        Method stringMethod = JsonWriter.class.getDeclaredMethod("string", String.class);
        stringMethod.setAccessible(true);

        StringWriter stringWriter = new StringWriter();
        outField.set(jsonWriter, stringWriter);

        char specialChar = '\u0080';

        String testValue = String.valueOf(specialChar);

        stringMethod.invoke(jsonWriter, testValue);

        String expectedOutput = "\"\u0080\"";
        assertEquals(expectedOutput, stringWriter.toString());
    }

    @Test
    public void testHtmlSafeOutput() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, IOException, NoSuchFieldException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        
        Field ht = JsonWriter.class.getDeclaredField("HTML_SAFE_REPLACEMENT_CHARS");
        ht.setAccessible(true);

        Field r_chars = JsonWriter.class.getDeclaredField("REPLACEMENT_CHARS");
        r_chars.setAccessible(true);

        String[] ht_string = (String[]) ht.get(jsonWriter);
        String[] r_string = (String[]) r_chars.get(jsonWriter);
        
        //testOutputWithHtmlSafe(writer, jsonWriter, true, ht_string);
        testOutputWithHtmlSafe(writer, jsonWriter, false, r_string);
    }

    private void testOutputWithHtmlSafe(StringWriter writer, JsonWriter jsonWriter, boolean htmlSafe, String[] expectedReplacements)
        throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, IOException, NoSuchFieldException {

        jsonWriter.setHtmlSafe(htmlSafe);

        Method stringMethod = JsonWriter.class.getDeclaredMethod("string", String.class);
        stringMethod.setAccessible(true);

        stringMethod.invoke(jsonWriter, "test");
        String input = "test";
        String expectedOutput = "\"test\"";
        if (htmlSafe) {
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                if (expectedReplacements[c] != null && c < 128) {
                    expectedOutput = expectedOutput.replace(String.valueOf(c), expectedReplacements[c]);
                }
            }
        }
        assertEquals(expectedOutput, writer.toString());
    }

    @Test
    public void testNormalClosure() throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException, IOException, InvocationTargetException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        Field stacksizeField = JsonWriter.class.getDeclaredField("stackSize");
        stacksizeField.setAccessible(true);

        jsonWriter.beginObject();

        jsonWriter.name("key1").value(true);
        jsonWriter.name("key2").value(false);

        jsonWriter.endObject();
        jsonWriter.close();

        int stackSize = stacksizeField.getInt(jsonWriter);

        assertEquals(stackSize, 0);

    }

    @Test
    public void testIncompleteDocumentEmptyStack() throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException, IOException, InvocationTargetException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        Field stacksizeField = JsonWriter.class.getDeclaredField("stackSize");
        stacksizeField.setAccessible(true);

        try {
            jsonWriter.close();
            fail("Expected IOException");
        } catch (IllegalStateException e) {
            assertEquals("Incomplete document", e.getMessage());
            int stackSize = stacksizeField.getInt(jsonWriter);
            assertEquals(stackSize, 1);
        } catch (IOException e) {
            assertEquals("Incomplete document", e.getMessage());
            int stackSize = stacksizeField.getInt(jsonWriter);
            assertEquals(stackSize, 1);
        }
    }

    @Test
    public void testIncompleteDocumentNonemptyStack() throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException, IOException, InvocationTargetException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        Field stacksizeField = JsonWriter.class.getDeclaredField("stackSize");
        stacksizeField.setAccessible(true);

        jsonWriter.beginObject();
        stacksizeField.setInt(jsonWriter, 2);

        try {
            jsonWriter.close();

            fail("Expected IOException");
        }catch (IllegalStateException e) {
            assertEquals("Incomplete document", e.getMessage());
            int stackSize = stacksizeField.getInt(jsonWriter);
            assertEquals(stackSize, 2);
        } catch (IOException e) {
            assertEquals("Incomplete document", e.getMessage());
            int stackSize = stacksizeField.getInt(jsonWriter);
            assertEquals(stackSize, 2);
        }
    }

    @Test
    public void testSetIndentNonEmpty() throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException, IOException, InvocationTargetException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        Field indentField = JsonWriter.class.getDeclaredField("indent");
        indentField.setAccessible(true);

        Field separatorField = JsonWriter.class.getDeclaredField("separator");
        separatorField.setAccessible(true);
        
        String nonEmptyIndent = "  ";
        assertTrue(nonEmptyIndent.length() >= 0);
        jsonWriter.setIndent(nonEmptyIndent);

        String indent = (String) indentField.get(jsonWriter);
        String separator = (String) separatorField.get(jsonWriter);

        assertEquals(nonEmptyIndent, indent);
        assertEquals(": ", separator);
    }

    @Test
    public void testSetIndentEmpty() throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException, IOException, InvocationTargetException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        Field indentField = JsonWriter.class.getDeclaredField("indent");
        indentField.setAccessible(true);

        Field separatorField = JsonWriter.class.getDeclaredField("separator");
        separatorField.setAccessible(true);
        
        String nonEmptyIndent = "  ";
        assertTrue(nonEmptyIndent.length() >= 0);
        jsonWriter.setIndent(nonEmptyIndent);

        String emptyIndent = "";
        jsonWriter.setIndent(emptyIndent);
        assertTrue(emptyIndent.length() >= 0);

        String indent = (String) indentField.get(jsonWriter);
        String separator = (String) separatorField.get(jsonWriter);

        assertNull(indent);
        assertEquals(":", separator);
    }

    @Test
    public void testStringMethodWithMutant() throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException, IOException, InvocationTargetException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        Method stringMethod = JsonWriter.class.getDeclaredMethod("string", String.class);
        stringMethod.setAccessible(true);

        Field outField = JsonWriter.class.getDeclaredField("out");
        outField.setAccessible(true);

        String inputString = "special\u2028characters\u2029";

        StringWriter stringWriter = new StringWriter();
        outField.set(jsonWriter, stringWriter);

        stringMethod.invoke(jsonWriter, inputString);

        String expectedOutput = "\"" + inputString + "\"";
        assertNotEquals(expectedOutput, stringWriter.toString());
    }

    @Test
    public void testContextNotEqualEmptyObject() throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException, IOException, InvocationTargetException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        Method peekMethod = JsonWriter.class.getDeclaredMethod("peek");
        peekMethod.setAccessible(true);

        Method beforeNameMethod = JsonWriter.class.getDeclaredMethod("beforeName");
        beforeNameMethod.setAccessible(true);

        Method replaceTopMethod = JsonWriter.class.getDeclaredMethod("replaceTop", int.class);
        replaceTopMethod.setAccessible(true);

        jsonWriter.beginObject();

        replaceTopMethod.invoke(jsonWriter, 1);

        try {

            Object resultObject = peekMethod.invoke(jsonWriter);

            int result = (int) (Integer) resultObject;
            assertEquals(result, 1);
            beforeNameMethod.invoke(jsonWriter);
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            assertEquals("Nesting problem.", e.getMessage());
        } catch (InvocationTargetException e) {
            //
        }

    }

    @Test
    public void testCloseContextNotEqualNonempty() throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException, IOException, InvocationTargetException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        Method peekMethod = JsonWriter.class.getDeclaredMethod("peek");
        peekMethod.setAccessible(true);

        Method closeMethod = JsonWriter.class.getDeclaredMethod("close", int.class, int.class, String.class);
        closeMethod.setAccessible(true);

        Field deferredNameField = JsonWriter.class.getDeclaredField("deferredName");
        deferredNameField.setAccessible(true);

        jsonWriter.beginObject();
        jsonWriter.name("key");

        int empty = 1;
        int nonempty = 3;

        try {
            Object resultObject = peekMethod.invoke(jsonWriter);
            int result = (int) (Integer) resultObject;
            assertEquals(result, nonempty);

            deferredNameField.set(jsonWriter, "key");
            closeMethod.invoke(jsonWriter, empty, nonempty, "}");
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            assertEquals("Nesting problem.", e.getMessage());
        } catch (InvocationTargetException e) {
            //
        }
    }


    @Test
    public void testNameStackSizeGreaterThanZero() throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException, IOException, InvocationTargetException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        Method nameMethod = JsonWriter.class.getDeclaredMethod("name", String.class);
        nameMethod.setAccessible(true);

        Field deferredNameField = JsonWriter.class.getDeclaredField("deferredName");
        deferredNameField.setAccessible(true);

        Field stackSizeField = JsonWriter.class.getDeclaredField("stackSize");
        stackSizeField.setAccessible(true);

        try {
            stackSizeField.set(jsonWriter, 1);

            nameMethod.invoke(jsonWriter, "key");
            int re = (int) (Integer) stackSizeField.getInt(jsonWriter);

            assertTrue(re > 0);
        } catch (IllegalStateException e) {
            assertEquals("JsonWriter is closed.", e.getMessage());
            fail("Unexpected IllegalStateException");
        } catch (InvocationTargetException e) {
            //
        }

        try {
            stackSizeField.set(jsonWriter, 0);
            int re = (int) (Integer) stackSizeField.getInt(jsonWriter);
            assertTrue(re == 0);

            nameMethod.invoke(jsonWriter, "key");
            fail("Unexpected IllegalStateException");
        } catch (IllegalStateException e) {
            assertEquals("JsonWriter is closed.", e.getMessage());
        } catch (InvocationTargetException e) {
            //
        }
    }

    @Test
    public void testFlushStackSizeLessThanZero() throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException, IOException, InvocationTargetException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        Method flushMethod = JsonWriter.class.getDeclaredMethod("flush");
        flushMethod.setAccessible(true);

        Field stackSizeField = JsonWriter.class.getDeclaredField("stackSize");
        stackSizeField.setAccessible(true);

        try {
            stackSizeField.set(jsonWriter, -1);

            flushMethod.invoke(jsonWriter);
        } catch (IllegalStateException e) {
            assertEquals("JsonWriter is closed.", e.getMessage());
            fail("Expected IllegalStateException");
        } catch (InvocationTargetException e) {
                //
        }

        try {
            stackSizeField.set(jsonWriter, 10);

            flushMethod.invoke(jsonWriter);
        } catch (IllegalStateException e) {
            assertEquals("JsonWriter is closed.", e.getMessage());
            fail("Expected IllegalStateException");
        } catch (InvocationTargetException e) {
                //
        }
    }

    @Test
    public void testPeekMutantStackSizeGreaterThanZero() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        Field stackSizeField = JsonWriter.class.getDeclaredField("stackSize");
        stackSizeField.setAccessible(true);

        Method peekMethod = JsonWriter.class.getDeclaredMethod("peek");
        peekMethod.setAccessible(true);

        stackSizeField.set(jsonWriter, 1);

        try {
            int result = (int) (Integer) peekMethod.invoke(jsonWriter);
            fail("Expected IllegalStateException for mutated condition");
        } catch (IllegalStateException e) {
            assertEquals("JsonWriter is closed.", e.getMessage());
        }
    }



}