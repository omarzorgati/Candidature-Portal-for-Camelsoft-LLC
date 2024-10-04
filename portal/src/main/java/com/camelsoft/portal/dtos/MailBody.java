package com.camelsoft.portal.dtos;

import lombok.Builder;


@Builder
public record MailBody(String to, String subject, String text) {
}
