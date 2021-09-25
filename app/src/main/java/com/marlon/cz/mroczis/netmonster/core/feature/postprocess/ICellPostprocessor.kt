package com.marlon.cz.mroczis.netmonster.core.feature.postprocess

import com.marlon.cz.mroczis.netmonster.core.model.cell.ICell

/**
 * Postprocessor allowing to change, intercept & change data
 */
interface ICellPostprocessor {

    /**
     * Postprocessing method that allows data modification within the list
     */
    fun postprocess(list: List<ICell>) : List<ICell>

}