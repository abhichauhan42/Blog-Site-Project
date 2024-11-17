import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate, useParams } from 'react-router-dom';
import './EditBlogPage.css'; 

function EditBlogPage() {
  const { id } = useParams();
  const [blog, setBlog] = useState({ blogName: '', body: '' });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null); 
  const navigate = useNavigate();

  useEffect(() => {
    const fetchBlog = async () => {
      const token = localStorage.getItem('token');
      try {
        const response = await axios.get(`http://localhost:8080/api/blogs/${id}`, {
          headers: { Authorization: `Bearer ${token}` }
        });
        setBlog(response.data); 
      } catch (err) {
        setError('Failed to load blog. Please try again.');
      } finally {
        setLoading(false);
      }
    };
    fetchBlog();
  }, [id]);

  const handleUpdateBlog = async (e) => {
    e.preventDefault();
    const token = localStorage.getItem('token');
    try {
      await axios.put(
        `http://localhost:8080/api/blogs/${id}`,
        { blogName: blog.blogName, body: blog.body },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      navigate('/dashboard'); 
    } catch (err) {
      setError('Failed to update blog. Please try again.');
    }
  };

  if (loading) {
    return <p>Loading blog...</p>;
  }

  if (error) {
    return <p className="error">{error}</p>;
  }

  return (
    <div className="form-container">
      <h2>Edit Blog</h2>
      <form onSubmit={handleUpdateBlog} className="edit-blog-form">
        <div className="form-group">
          <label htmlFor="blogName">Blog Name</label>
          <input
            type="text"
            id="blogName"
            placeholder="Blog Name"
            value={blog.blogName}
            onChange={(e) => setBlog({ ...blog, blogName: e.target.value })}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="body">Blog Body</label>
          <textarea
            id="body"
            placeholder="Blog Body"
            value={blog.body}
            onChange={(e) => setBlog({ ...blog, body: e.target.value })}
            required
          />
        </div>
        <button type="submit" className="submit-btn">Update</button>
      </form>
    </div>
  );
}

export default EditBlogPage;
