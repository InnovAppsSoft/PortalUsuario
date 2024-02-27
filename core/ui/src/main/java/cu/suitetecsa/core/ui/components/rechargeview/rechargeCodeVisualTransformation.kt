package cu.suitetecsa.core.ui.components.rechargeview

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

private const val MaxCodeLength = 16
private const val ChunkSize = 4
private const val SpaceAfterEachChunk = 1
private const val LastChunkIndex = MaxCodeLength - 1
private const val MaxTransformedLength = MaxCodeLength + (MaxCodeLength / ChunkSize - 1)

fun rechargeCodeVisualTransformation() = VisualTransformation { text ->
    val trimmed = if (text.text.length >= MaxCodeLength) text.text.substring(0, MaxCodeLength) else text.text
    var out = ""
    for (i in trimmed.indices) {
        out += trimmed[i]
        if (i % ChunkSize == ChunkSize - 1 && i != LastChunkIndex) out += " "
    }

    val rechargeCodeOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            val extraSpaces = offset / ChunkSize
            return offset + extraSpaces.coerceAtMost(MaxCodeLength / ChunkSize - 1)
        }

        override fun transformedToOriginal(offset: Int): Int {
            val removedSpaces = offset / (ChunkSize + SpaceAfterEachChunk)
            return offset - removedSpaces.coerceAtMost(MaxTransformedLength / (ChunkSize + SpaceAfterEachChunk))
        }
    }
    TransformedText(AnnotatedString(out), rechargeCodeOffsetTranslator)
}
