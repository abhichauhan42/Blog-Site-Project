import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './CreateBlogPage.css';  

function CreateBlogPage() {
  const [blogName, setBlogName] = useState('');
  const [body, setBody] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [wordCount, setWordCount] = useState(0); 
  const navigate = useNavigate();

  const handleCreateBlog = async (e) => {
    e.preventDefault();
    setErrorMessage(''); 

    if (wordCount > 500) {
      setErrorMessage('The blog body cannot exceed 500 words.');
      return;
    }

    if (!blogName || !body) {
      setErrorMessage('Both fields are required.');
      return;
    }

    const token = localStorage.getItem('token');
    if (!token) {
      setErrorMessage('You must be logged in to create a blog.');
      return;
    }

    try {
      const userResponse = await axios.get('http://localhost:8080/api/users/profile', {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (userResponse.status === 200) {
        const userId = userResponse.data.id;  

        const response = await axios.post(
          'http://localhost:8080/api/blogs',
          { blogName, body, userId },  
          {
            headers: {
              Authorization: `Bearer ${token}`,  
            },
          }
        );

        if (response.status === 201) {
          setBlogName('');
          setBody('');
          setWordCount(0);
          navigate('/dashboard');  
        }
      } else {
        setErrorMessage('Unable to fetch user details. Please try again.');
      }
    } catch (error) {
      console.error('Error creating blog:', error);
      setErrorMessage('An error occurred while creating the blog. Please try again.');
    }
  };

  const handleBodyChange = (e) => {
    const newBody = e.target.value;
    setBody(newBody);

    const words = newBody.trim().split(/\s+/);
    setWordCount(words.length);
  };

  return (
    <div className="form-container">
      <h2>Create New Blog</h2>
      <form onSubmit={handleCreateBlog}>
        <div className="form-group">
          <input
            type="text"
            placeholder="Blog Name"
            value={blogName}
            onChange={(e) => setBlogName(e.target.value)}
            required
          />
        </div>
        <div className="form-group">
          <textarea
            placeholder="Blog Body"
            value={body}
            onChange={handleBodyChange}
            required
          />
          <div className="word-count">
            Word count: {wordCount} / 500
          </div>
        </div>
        {errorMessage && <div className="error-message">{errorMessage}</div>}
        <button type="submit" disabled={wordCount > 500}>Create Blog</button>
      </form>
    </div>
  );
}

export default CreateBlogPage;
