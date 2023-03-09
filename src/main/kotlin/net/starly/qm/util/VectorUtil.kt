package net.starly.qm.util

import org.bukkit.util.Vector
import kotlin.math.abs
import kotlin.math.sqrt

object VectorUtil {
    val ZERO: Vector get() = Vector(0, 0, 0)
    val UP: Vector get() = Vector(0, 1, 0)
    val DOWN: Vector get() = Vector(0, -1, 0)
    val LEFT: Vector get() = Vector(-1, 0, 0)
    val RIGHT: Vector get() = Vector(1, 0, 0)

    // 오른손 좌표계이므로
    val BACK: Vector get() = Vector(0, 0, 1)
    val FORWARD: Vector get() = Vector(0, 0, -1)

    val Vector.normalized: Vector
        get() = clone().normalize()

    val standardBasisVectors; get() = arrayOf(
        RIGHT,
        UP,
        BACK,
    )

    fun Vector.set(value: Vector): Vector {
        x = value.x
        y = value.y
        z = value.z
        return this
    }
    fun Vector.set(x: Double = this.x, y: Double = this.y, z: Double = this.z): Vector {
        this.x = x
        this.y = y
        this.z = z
        return this
    }

    operator fun Vector.get(index: Int) = when(index) {
        0 -> x
        1 -> y
        2 -> z
        else -> error("invalid index call on vector(${index})")
    }
    operator fun Vector.set(index: Int, value: Double): Vector {
        when(index) {
            0 -> x = value
            1 -> y = value
            2 -> z = value
            else -> {}
        }
        return this
    }

    fun Vector.length(newLength: Double): Vector {
        val lengthSquared = lengthSquared()
        // 영벡터는 방향이 없음
        if(lengthSquared <= 0.0) return this
        val invLength = 1.0 / sqrt(lengthSquared)
        return multiply(newLength * invLength)
    }

    fun getRotationMatrix(forward: Vector, up: Vector = UP): Matrix3x3 {
        val localZ = forward.normalized
        // 주어진 업벡터와 거의 일치하는 경우 임의의 x축을 제공
        val localX = if (abs(localZ.dot(up)) >= (1.0 - 0.0001)) {
            RIGHT // 근데 이게 맞는지는 모르겠다 ㅎㅎ;
        } else {
            up.getCrossProduct(localZ).normalize()
        }
        val localY = localZ.getCrossProduct(localX).normalize()
        return Matrix3x3(localX, localY, localZ)
    }
}