import React, {useEffect, useState} from 'react';
import Papa from 'papaparse';

// const newsData = [
//   { category: '기술', title: '기술 뉴스 1', negativity: '긍정' },
//   { category: '정치', title: '정치 뉴스 1', negativity: '부정' },
//   { category: '연예', title: '연예 뉴스 1', negativity: '긍정' },
//   // ...more news items
// ];

function NewsTable() {
  // State to track which comment sections are visible
  const [newsData, setNewsData] = useState([]);
  const [visibleComments, setVisibleComments] = useState([]);

  useEffect(() => {
    Papa.parse('csv/test.csv', {
      download: true,
      header: true,
      complete: (results) => {
        // console.log(results.data)
        setNewsData(results.data);
        setVisibleComments(Array(results.data.length).fill(false));
      }
    });
  }, []);

  // // Toggle the visibility of a comment section
  // const toggleComments = (index) => {
  //   setVisibleComments(visibleComments.map((visible, i) => (i === index ? !visible : visible)));
  // };

  return (
    <table>
      <thead>
      <tr>
        <th>카테고리</th>
        <th>제목</th>
        <th>긍정도</th>
        {/*<th>댓글</th>*/}
      </tr>
      </thead>
      <tbody>
      {newsData.map((item, index) => (
        index !== newsData.length - 1 && (
          <React.Fragment key={index}>
            <tr>
              <td>{item.category}</td>
              <td><a href={item.link} className="news-title">{item.title}</a></td>
              <td>{item.reaction === 0 ? '긍정' : '부정'}</td>
              {/*<td><button onClick={() => toggleComments(index)} className="toggle-comment">댓글 보기</button></td>*/}
            </tr>
            {/*{visibleComments[index] && (*/}
            {/*  <tr className="comment-row">*/}
            {/*    <td colSpan="4" className="comment-section">*/}
            {/*      <div className="comment">"Really insightful article, thanks for sharing!"</div>*/}
            {/*      <div className="comment">"I have a different perspective. Here's my take..."</div>*/}
            {/*      /!* Add more comments as needed *!/*/}
            {/*    </td>*/}
            {/*  </tr>*/}
            {/*)}*/}
          </React.Fragment>
        )
      ))}
      </tbody>
    </table>
  );
}

export default NewsTable;
