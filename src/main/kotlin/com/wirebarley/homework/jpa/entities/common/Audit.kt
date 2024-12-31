package com.wirebarley.homework.jpa.entities.common

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.time.LocalDateTime

/**
 * 감사 기록용 Embeddable 엔티티.
 *
 * @property createdAt 최초생성시각
 * @property createdBy 최초생성자
 * @property updatedAt 최종수정시각
 * @property updatedBy 최종수정자
 */
@Embeddable
class Audit(

  @Column(name = "created_at", nullable = false)
  val createdAt: LocalDateTime,

  @Column(name = "created_by", nullable = false)
  val createdBy: String,

  @Column(name = "updated_at", nullable = false)
  var updatedAt: LocalDateTime,

  @Column(name = "updated_by", nullable = false)
  var updatedBy: String,

  ) {

  companion object {
    /**
     * 감사 기록용 객체를 생성하기 위한 팩토리 메소드입니다.
     *
     * @param requester 요청자
     * @return 감사 기록용 객체
     */
    fun create(requester: String): Audit {
      val now = LocalDateTime.now()

      return Audit(
        createdAt = now,
        createdBy = requester,
        updatedAt = now,
        updatedBy = requester,
      )
    }
  }

  /**
   * 최종수정시각 변경을 위한 메소드입니다.
   *
   * @param requester 요청자
   */
  fun update(requester: String) {
    this.updatedAt = LocalDateTime.now()
    this.updatedBy = requester
  }
}
