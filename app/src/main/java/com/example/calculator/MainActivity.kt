package com.example.calculator

import android.media.VolumeShaper.Operation
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    private lateinit var input1: EditText
    private lateinit var input2: EditText
    private lateinit var operation: TextView
    private lateinit var result: TextView
    private var action: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        input1 = findViewById(R.id.input1)
        input1.showSoftInputOnFocus = false
        input1.requestFocus()

        input2 = findViewById(R.id.input2)
        input2.showSoftInputOnFocus = false


        val checkZeroDivision: () -> Boolean = {
            val isDivision = operation.text.toString() == "/"
            val isNull = if(input2.text.isNotEmpty()) {
                input2.text.toString().toDouble().equals(0.0)
            } else {
                false
            }
           isDivision && isNull
        }
        val setZeroDivisionError = {
            if(checkZeroDivision()) {
                input2.error = "Деление на ноль"
            } else {
                null
            }
        }

        operation = findViewById(R.id.operation)

        operation.doAfterTextChanged {
            if(!operation.text.isEmpty()) {
                operation.error = null
            }
            setZeroDivisionError()
        }
        result = findViewById(R.id.result)


        input2.setOnFocusChangeListener { _, isFocused ->
            if(!isFocused) return@setOnFocusChangeListener

            if(operation.text.isEmpty()) {
                operation.error = "Не выбрана операция"
            } else {
                operation.error = null
            }
        }
        input2.doAfterTextChanged {  setZeroDivisionError() }

        val btn0: Button = findViewById(R.id.btn0)
        val btn1: Button = findViewById(R.id.btn1)
        val btn2: Button = findViewById(R.id.btn2)
        val btn3: Button = findViewById(R.id.btn3)
        val btn4: Button = findViewById(R.id.btn4)
        val btn5: Button = findViewById(R.id.btn5)
        val btn6: Button = findViewById(R.id.btn6)
        val btn7: Button = findViewById(R.id.btn7)
        val btn8: Button = findViewById(R.id.btn8)
        val btn9: Button = findViewById(R.id.btn9)
        val btnDot: Button = findViewById(R.id.btnDot)
        val btnBS: Button = findViewById(R.id.btnBS)

        val btnPlus: Button = findViewById(R.id.btnPlus)
        val btnMinus: Button = findViewById(R.id.btnMinus)
        val btnMultiply: Button = findViewById(R.id.btnMultiply)
        val btnDivision: Button = findViewById(R.id.btnDivision)
        val btnResult: Button = findViewById(R.id.btnResult)

        val addChar = { value: String ->
            val input = getFocusedInput()
            input.text.insert(input.selectionStart, value)
        }

        btn0.setOnClickListener { addChar("0") }
        btn1.setOnClickListener { addChar("1") }
        btn2.setOnClickListener { addChar("2") }
        btn3.setOnClickListener { addChar("3") }
        btn4.setOnClickListener { addChar("4") }
        btn5.setOnClickListener { addChar("5") }
        btn6.setOnClickListener { addChar("6") }
        btn7.setOnClickListener { addChar("7") }
        btn8.setOnClickListener { addChar("8") }
        btn9.setOnClickListener { addChar("9") }
        btnDot.setOnClickListener { addChar(".") }
        btnBS.setOnClickListener {
            val input = getFocusedInput()
            if(input.text.isNotEmpty()) {
                var s = input.text.toString()
                input.run {
                    var i = selectionStart
                    s = s.substring(0, i - 1) + s.substring(i)
                    text.clear()
                    text.append(s)
                }
            }
        }

        val swapInputAndResult = {
            if(result.text.isNotEmpty() && input1.text.isEmpty()) {
                input1.run {
                    text.clear()
                    append(result.text)
                }
                result.text = ""
            }
        }
        val doOperation: (String) -> Unit = { btnValue: String ->
            if(input1.text.isEmpty() || input2.text.isEmpty() || operation.text.isEmpty()) {
                swapInputAndResult()
                operation.text = btnValue
            } else {
                var res = calcRes(
                    input1.text.toString().toDouble(),
                    input2.text.toString().toDouble(),
                    operation.text.toString()
                )
                operation.text = btnValue
                input1.run {
                    text.clear()
                    text.append(res)
                }
                input2.text.clear()
            }

            val input1IsEmpty: Boolean = input1.text.isEmpty()
            val input2IsEmpty: Boolean = input2.text.isEmpty()
            if (input1IsEmpty || input2IsEmpty) {
                if(input1.isFocused && !input1IsEmpty) {
                    input2.requestFocus()
                } else if (input2.isFocused && !input2IsEmpty) {
                    input1.requestFocus()
                }
            }

        }

        btnPlus.setOnClickListener { if (!checkZeroDivision()) doOperation("+") }
        btnMinus.setOnClickListener { if (!checkZeroDivision()) doOperation("-") }
        btnMultiply.setOnClickListener { if (!checkZeroDivision()) doOperation("*") }
        btnDivision.setOnClickListener { if (!checkZeroDivision()) doOperation("/") }
        btnResult.setOnClickListener {
            val input1IsEmpty: Boolean = input1.text.isEmpty()
            val input2IsEmpty: Boolean = input2.text.isEmpty()
            val operationIsEmpty: Boolean = operation.text.isEmpty()
            if(input1IsEmpty) {
                input1.error = "Введите число!"
            }
            if(input2IsEmpty) {
                input2.error = "Введите число!"
            }
            if(operationIsEmpty) {
                operation.error = "Не выбрана операция"
            }
            if(!input1IsEmpty && !input2IsEmpty && !operationIsEmpty && !checkZeroDivision()) {
                var res = calcRes(
                    input1.text.toString().toDouble(),
                    input2.text.toString().toDouble(),
                    operation.text.toString()
                )
                result.text = res
                input1.run {
                    text.clear()
                    requestFocus()
                }
                input2.text.clear()
                operation.text = ""

            }

        }
    }

    private fun calcRes(a: Double, b: Double, operation: String): String {
        var res: Double = when(operation) {
            "+" -> a + b
            "-" -> a - b
            "*" -> a * b
            "/" ->
                if (a != 0.0 && b != 0.0) {
                    a / b
                } else {
                    0.0
                }

            else -> 0.0
        }

        return if (ceil(res) == floor(res)) {
            res.toInt().toString()
        } else {
            ((res * 1000).roundToInt() / 1000.0).toString()
        }
    }

    private fun getFocusedInput(): EditText {
        return if(input2.isFocused) {
            input2
        } else {
            input1.requestFocus()
            input1
        }
    }
}