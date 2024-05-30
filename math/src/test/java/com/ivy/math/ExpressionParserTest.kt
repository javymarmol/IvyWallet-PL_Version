package com.ivy.math

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.ivy.parser.Parser
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

/**
 * Created by Heyner Javier Marmol @javymarmol on 29/05/24.
 * javymarmol.com
 * Copyright (c) 2024 JavyMarmol. All rights reserved.
 */
class ExpressionParserTest {
    private lateinit var parser: Parser<TreeNode>

    @BeforeEach
    fun setUp() {
        parser = expressionParser()
    }

    @ParameterizedTest
    @CsvSource(
        "3+6/3-(-10), 15.0",
        "5+6, 11.0",
        "5.000000, 5.0",
        "100/(10*10), 1.0",
    )
    fun `test evaluating expression`(expression: String, expected: Double) {
        val result = parser(expression).first()

        val actual = result.value.eval()

        assertThat(actual).isEqualTo(expected)
    }
}