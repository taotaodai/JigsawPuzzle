package com.ttd.jipu.utils

import com.ttd.jipu.entity.Question
import com.ttd.jipu.R


/**
 * Created by wt on 2018/4/25.
 */
class QuestionBank {
    companion object {
        fun getQuestions(): List<Question> {
            var questions: List<Question> = listOf()

            /**
             * 3*3
             */
            questions += Question(R.drawable.a_2)
            /**
             * 3*3  黑白
             */
            val q2 = Question(R.drawable.a_3, 0.0f)
            q2.shelterCount = 2
            questions += q2
            /**
             * 4*4
             */
            val q3 = Question(R.drawable.a_4, 4)
            q3.shelterCount = 2
            questions += q3
            /**
             * 4*4  加遮挡物(3个)
             */
            var q4 = Question(R.drawable.a_5, 3, 4)
            q4.shelterCount = 1
            questions += q4

            return questions
        }
    }
}