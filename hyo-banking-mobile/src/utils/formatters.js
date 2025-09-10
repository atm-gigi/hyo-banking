export const formatAmount = amount => {
  return `${(amount / 10000).toLocaleString()}만원`;
};

export const formatDate = dateString => {
  const date = new Date(dateString);
  const now = new Date();
  const diffTime = Math.abs(now - date);
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

  if (diffDays === 1) {
    return '오늘';
  } else if (diffDays === 2) {
    return '어제';
  } else if (diffDays <= 7) {
    return `${diffDays - 1}일 전`;
  } else {
    return date.toLocaleDateString('ko-KR', { month: 'short', day: 'numeric' });
  }
};
