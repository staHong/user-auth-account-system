package com.ds.auth.repository;

import com.ds.auth.entity.InquiryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InquiryRepository extends JpaRepository<InquiryEntity, Long>, JpaSpecificationExecutor<InquiryEntity> {
}
